import os
import sys

sys.path.insert(1, os.path.join(sys.path[0], "../../helpers"))
from helpers import helm_template
import yaml

clusterName = "elasticsearch"
nodeGroup = "master"
uname = clusterName + "-" + nodeGroup


def test_defaults():
    config = """
    """

    r = helm_template(config)

    # Statefulset
    assert r["statefulset"][uname]["spec"]["replicas"] == 3
    assert r["statefulset"][uname]["spec"]["updateStrategy"] == {
        "type": "RollingUpdate"
    }
    assert r["statefulset"][uname]["spec"]["podManagementPolicy"] == "Parallel"
    assert r["statefulset"][uname]["spec"]["serviceName"] == uname + "-headless"
    assert r["statefulset"][uname]["spec"]["template"]["spec"]["affinity"][
        "podAntiAffinity"
    ]["requiredDuringSchedulingIgnoredDuringExecution"][0] == {
        "labelSelector": {
            "matchExpressions": [{"key": "app", "operator": "In", "values": [uname]}]
        },
        "topologyKey": "kubernetes.io/hostname",
    }

    # Default environment variables
    env_vars = [
        {
            "name": "node.name",
            "valueFrom": {"fieldRef": {"fieldPath": "metadata.name"}},
        },
        {
            "name": "cluster.initial_master_nodes",
            "value": uname + "-0," + uname + "-1," + uname + "-2,",
        },
        {"name": "discovery.seed_hosts", "value": uname + "-headless"},
        {"name": "network.host", "value": "0.0.0.0"},
        {"name": "cluster.name", "value": clusterName},
        {
            "name": "node.roles",
            "value": "master,data,data_content,data_hot,data_warm,data_cold,ingest,ml,remote_cluster_client,transform,",
        },
    ]

    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    for env in env_vars:
        assert env in c["env"]

    # Image
    assert c["image"].startswith("docker.elastic.co/elasticsearch/elasticsearch:")
    assert c["imagePullPolicy"] == "IfNotPresent"
    assert c["name"] == "elasticsearch"

    # Ports
    assert c["ports"][0] == {"name": "http", "containerPort": 9200}
    assert c["ports"][1] == {"name": "transport", "containerPort": 9300}

    # Health checks
    assert c["readinessProbe"]["failureThreshold"] == 3
    assert c["readinessProbe"]["initialDelaySeconds"] == 10
    assert c["readinessProbe"]["periodSeconds"] == 10
    assert c["readinessProbe"]["successThreshold"] == 3
    assert c["readinessProbe"]["timeoutSeconds"] == 5

    assert "curl" in c["readinessProbe"]["exec"]["command"][-1]
    assert "https://127.0.0.1:9200" in c["readinessProbe"]["exec"]["command"][-1]

    # Resources
    assert c["resources"] == {
        "requests": {"cpu": "1000m", "memory": "2Gi"},
        "limits": {"cpu": "1000m", "memory": "2Gi"},
    }

    # Mounts
    assert c["volumeMounts"][0]["mountPath"] == "/usr/share/elasticsearch/data"
    assert c["volumeMounts"][0]["name"] == uname

    # volumeClaimTemplates
    v = r["statefulset"][uname]["spec"]["volumeClaimTemplates"][0]
    assert v["metadata"]["name"] == uname
    assert "labels" not in v["metadata"]
    assert v["spec"]["accessModes"] == ["ReadWriteOnce"]
    assert v["spec"]["resources"]["requests"]["storage"] == "30Gi"

    # Init container
    i = r["statefulset"][uname]["spec"]["template"]["spec"]["initContainers"][0]
    assert i["name"] == "configure-sysctl"
    assert i["command"] == ["sysctl", "-w", "vm.max_map_count=262144"]
    assert i["image"].startswith("docker.elastic.co/elasticsearch/elasticsearch:")
    assert i["securityContext"] == {"privileged": True, "runAsUser": 0}

    # Other
    assert r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"] == {
        "fsGroup": 1000,
        "runAsUser": 1000,
    }
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"][
            "terminationGracePeriodSeconds"
        ]
        == 120
    )

    # Pod disruption budget
    assert r["poddisruptionbudget"][uname + "-pdb"]["spec"]["maxUnavailable"] == 1

    # Service
    s = r["service"][uname]
    assert s["metadata"]["name"] == uname
    assert s["metadata"]["annotations"] == {}
    assert s["spec"]["type"] == "ClusterIP"
    assert s["spec"]["publishNotReadyAddresses"] == False
    assert len(s["spec"]["ports"]) == 2
    assert s["spec"]["ports"][0] == {"name": "http", "port": 9200, "protocol": "TCP"}
    assert s["spec"]["ports"][1] == {
        "name": "transport",
        "port": 9300,
        "protocol": "TCP",
    }
    assert "loadBalancerSourceRanges" not in s["spec"]

    # Headless Service
    h = r["service"][uname + "-headless"]
    assert h["spec"]["clusterIP"] == "None"
    assert h["spec"]["publishNotReadyAddresses"] == True
    assert h["spec"]["ports"][0]["name"] == "http"
    assert h["spec"]["ports"][0]["port"] == 9200
    assert h["spec"]["ports"][1]["name"] == "transport"
    assert h["spec"]["ports"][1]["port"] == 9300

    # Empty customizable defaults
    assert "imagePullSecrets" not in r["statefulset"][uname]["spec"]["template"]["spec"]
    assert "tolerations" not in r["statefulset"][uname]["spec"]["template"]["spec"]
    assert "nodeSelector" not in r["statefulset"][uname]["spec"]["template"]["spec"]
    assert "ingress" not in r
    assert "hostAliases" not in r["statefulset"][uname]["spec"]["template"]["spec"]


def test_increasing_the_replicas():
    config = """
replicas: 5
"""
    r = helm_template(config)
    assert r["statefulset"][uname]["spec"]["replicas"] == 5


def test_disabling_pod_disruption_budget():
    config = """
maxUnavailable: false
"""
    r = helm_template(config)
    assert "poddisruptionbudget" not in r


def test_overriding_the_image_and_tag():
    config = """
image: customImage
imageTag: 6.2.4
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]["image"]
        == "customImage:6.2.4"
    )


def test_set_initial_master_nodes():
    config = """
roles:
  - master
"""
    r = helm_template(config)
    env = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]["env"]
    assert {
        "name": "cluster.initial_master_nodes",
        "value": "elasticsearch-master-0,"
        + "elasticsearch-master-1,"
        + "elasticsearch-master-2,",
    } in env

    for e in env:
        assert e["name"] != "discovery.zen.minimum_master_nodes"


def test_dont_set_initial_master_nodes_if_not_master():
    config = """
roles:
  - data
"""
    r = helm_template(config)
    env = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]["env"]
    for e in env:
        assert e["name"] != "cluster.initial_master_nodes"


def test_set_discovery_seed_host():
    config = """
roles:
  - master
"""
    r = helm_template(config)
    env = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]["env"]
    assert {
        "name": "discovery.seed_hosts",
        "value": "elasticsearch-master-headless",
    } in env

    for e in env:
        assert e["name"] != "discovery.zen.ping.unicast.hosts"


def test_adding_extra_env_vars():
    config = """
extraEnvs:
  - name: hello
    value: world
"""
    r = helm_template(config)
    env = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]["env"]
    assert {"name": "hello", "value": "world"} in env


def test_adding_env_from():
    config = """
envFrom:
- secretRef:
    name: secret-name
"""
    r = helm_template(config)
    secretRef = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0][
        "envFrom"
    ][0]["secretRef"]
    assert secretRef == {"name": "secret-name"}


def test_adding_a_extra_volume_with_volume_mount():
    config = """
extraVolumes: |
  - name: extras
    emptyDir: {}
extraVolumeMounts: |
  - name: extras
    mountPath: /usr/share/extras
    readOnly: true
"""
    r = helm_template(config)
    extraVolume = r["statefulset"][uname]["spec"]["template"]["spec"]["volumes"]
    assert {"name": "extras", "emptyDir": {}} in extraVolume
    extraVolumeMounts = r["statefulset"][uname]["spec"]["template"]["spec"][
        "containers"
    ][0]["volumeMounts"]
    assert {
        "name": "extras",
        "mountPath": "/usr/share/extras",
        "readOnly": True,
    } in extraVolumeMounts


def test_adding_a_extra_volume_with_volume_mount_as_yaml():
    config = """
extraVolumes:
  - name: extras
    emptyDir: {}
extraVolumeMounts:
  - name: extras
    mountPath: /usr/share/extras
    readOnly: true
"""
    r = helm_template(config)
    extraVolume = r["statefulset"][uname]["spec"]["template"]["spec"]["volumes"]
    assert {"name": "extras", "emptyDir": {}} in extraVolume
    extraVolumeMounts = r["statefulset"][uname]["spec"]["template"]["spec"][
        "containers"
    ][0]["volumeMounts"]
    assert {
        "name": "extras",
        "mountPath": "/usr/share/extras",
        "readOnly": True,
    } in extraVolumeMounts


def test_adding_a_extra_container():
    config = """
extraContainers: |
  - name: do-something
    image: busybox
    command: ['do', 'something']
"""
    r = helm_template(config)
    extraContainer = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"]
    assert {
        "name": "do-something",
        "image": "busybox",
        "command": ["do", "something"],
    } in extraContainer


def test_adding_a_extra_container_as_yaml():
    config = """
extraContainers:
  - name: do-something
    image: busybox
    command: ['do', 'something']
"""
    r = helm_template(config)
    extraContainer = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"]
    assert {
        "name": "do-something",
        "image": "busybox",
        "command": ["do", "something"],
    } in extraContainer


def test_adding_a_extra_init_container():
    config = """
extraInitContainers: |
  - name: do-something
    image: busybox
    command: ['do', 'something']
"""
    r = helm_template(config)
    extraInitContainer = r["statefulset"][uname]["spec"]["template"]["spec"][
        "initContainers"
    ]
    assert {
        "name": "do-something",
        "image": "busybox",
        "command": ["do", "something"],
    } in extraInitContainer


def test_adding_a_extra_init_container_as_yaml():
    config = """
extraInitContainers:
  - name: do-something
    image: busybox
    command: ['do', 'something']
"""
    r = helm_template(config)
    extraInitContainer = r["statefulset"][uname]["spec"]["template"]["spec"][
        "initContainers"
    ]
    assert {
        "name": "do-something",
        "image": "busybox",
        "command": ["do", "something"],
    } in extraInitContainer


def test_sysctl_init_container_disabled():
    config = """
sysctlInitContainer:
  enabled: false
"""
    r = helm_template(config)
    assert "initContainers" not in r["statefulset"][uname]["spec"]["template"]["spec"]


def test_sysctl_init_container_enabled():
    config = """
sysctlInitContainer:
  enabled: true
"""
    r = helm_template(config)
    initContainers = r["statefulset"][uname]["spec"]["template"]["spec"][
        "initContainers"
    ]
    assert initContainers[0]["name"] == "configure-sysctl"


def test_sysctl_init_container_image():
    config = """
image: customImage
imageTag: 6.2.4
imagePullPolicy: Never
sysctlInitContainer:
  enabled: true
"""
    r = helm_template(config)
    initContainers = r["statefulset"][uname]["spec"]["template"]["spec"][
        "initContainers"
    ]
    assert initContainers[0]["image"] == "customImage:6.2.4"
    assert initContainers[0]["imagePullPolicy"] == "Never"


def test_adding_storageclass_annotation_to_volumeclaimtemplate():
    config = """
persistence:
  annotations:
    volume.beta.kubernetes.io/storage-class: id
"""
    r = helm_template(config)
    annotations = r["statefulset"][uname]["spec"]["volumeClaimTemplates"][0][
        "metadata"
    ]["annotations"]
    assert annotations["volume.beta.kubernetes.io/storage-class"] == "id"


def test_adding_multiple_persistence_annotations():
    config = """
    persistence:
      annotations:
        hello: world
        world: hello
    """
    r = helm_template(config)
    annotations = r["statefulset"][uname]["spec"]["volumeClaimTemplates"][0][
        "metadata"
    ]["annotations"]

    assert annotations["hello"] == "world"
    assert annotations["world"] == "hello"


def test_enabling_persistence_label_in_volumeclaimtemplate():
    config = """
persistence:
  labels:
    enabled: true
"""
    r = helm_template(config)
    volume_claim_template_labels = r["statefulset"][uname]["spec"][
        "volumeClaimTemplates"
    ][0]["metadata"]["labels"]
    statefulset_labels = r["statefulset"][uname]["metadata"]["labels"]
    expected_labels = statefulset_labels
    # heritage label shouldn't be present in volumeClaimTemplates labels
    expected_labels.pop("heritage")
    assert volume_claim_template_labels == expected_labels


def test_adding_a_secret_mount():
    config = """
secretMounts:
  - name: elastic-certificates
    secretName: elastic-certs
    path: /usr/share/elasticsearch/config/certs
"""
    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert s["containers"][0]["volumeMounts"][-1] == {
        "mountPath": "/usr/share/elasticsearch/config/certs",
        "name": "elastic-certificates",
    }
    assert {
        "name": "elastic-certificates",
        "secret": {"secretName": "elastic-certs"},
    } in s["volumes"]


def test_adding_a_secret_mount_with_subpath():
    config = """
secretMounts:
  - name: elastic-certificates
    secretName: elastic-certs
    path: /usr/share/elasticsearch/config/certs
    subPath: cert.crt
"""
    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert s["containers"][0]["volumeMounts"][-1] == {
        "mountPath": "/usr/share/elasticsearch/config/certs",
        "subPath": "cert.crt",
        "name": "elastic-certificates",
    }


def test_adding_a_secret_mount_with_default_mode():
    config = """
secretMounts:
  - name: elastic-certificates
    secretName: elastic-certs
    path: /usr/share/elasticsearch/config/certs
    subPath: cert.crt
    defaultMode: 0755
"""
    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert s["containers"][0]["volumeMounts"][-1] == {
        "mountPath": "/usr/share/elasticsearch/config/certs",
        "subPath": "cert.crt",
        "name": "elastic-certificates",
    }


def test_adding_image_pull_secrets():
    config = """
imagePullSecrets:
  - name: test-registry
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["imagePullSecrets"][0][
            "name"
        ]
        == "test-registry"
    )


def test_adding_tolerations():
    config = """
tolerations:
- key: "key1"
  operator: "Equal"
  value: "value1"
  effect: "NoExecute"
  tolerationSeconds: 3600
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["tolerations"][0]["key"]
        == "key1"
    )


def test_adding_pod_annotations():
    config = """
podAnnotations:
  iam.amazonaws.com/role: es-role
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["metadata"]["annotations"][
            "iam.amazonaws.com/role"
        ]
        == "es-role"
    )


def test_adding_serviceaccount_annotations():
    config = """
rbac:
  create: true
  serviceAccountAnnotations:
    eks.amazonaws.com/role-arn: arn:aws:iam::111111111111:role/k8s.clustername.namespace.serviceaccount
"""
    r = helm_template(config)
    assert (
        r["serviceaccount"][uname]["metadata"]["annotations"][
            "eks.amazonaws.com/role-arn"
        ]
        == "arn:aws:iam::111111111111:role/k8s.clustername.namespace.serviceaccount"
    )


def test_adding_a_node_selector():
    config = """
nodeSelector:
  disktype: ssd
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["nodeSelector"]["disktype"]
        == "ssd"
    )


def test_adding_resources_to_initcontainer():
    config = """
initResources:
  limits:
    cpu: "25m"
    memory: "128Mi"
  requests:
    cpu: "25m"
    memory: "128Mi"
"""
    r = helm_template(config)
    i = r["statefulset"][uname]["spec"]["template"]["spec"]["initContainers"][0]

    assert i["resources"] == {
        "requests": {"cpu": "25m", "memory": "128Mi"},
        "limits": {"cpu": "25m", "memory": "128Mi"},
    }


def test_adding_a_node_affinity():
    config = """
nodeAffinity:
  preferredDuringSchedulingIgnoredDuringExecution:
  - weight: 100
    preference:
      matchExpressions:
      - key: mylabel
        operator: In
        values:
        - myvalue
"""
    r = helm_template(config)
    assert r["statefulset"][uname]["spec"]["template"]["spec"]["affinity"][
        "nodeAffinity"
    ] == {
        "preferredDuringSchedulingIgnoredDuringExecution": [
            {
                "weight": 100,
                "preference": {
                    "matchExpressions": [
                        {"key": "mylabel", "operator": "In", "values": ["myvalue"]}
                    ]
                },
            }
        ]
    }


def test_adding_an_ingress_rule():
    config = """
ingress:
  enabled: true
  annotations:
    kubernetes.io/ingress.class: nginx
  hosts:
    - host: elasticsearch.elastic.co
      paths:
        - path: /
    - host: ''
      paths:
        - path: /
        - path: /mypath
          servicePort: 8888
    - host: elasticsearch.hello.there
      paths:
        - path: /
          servicePort: 9999
  tls:
  - secretName: elastic-co-wildcard
    hosts:
     - elasticsearch.elastic.co
"""

    r = helm_template(config)
    assert uname in r["ingress"]
    i = r["ingress"][uname]["spec"]
    assert i["tls"][0]["hosts"][0] == "elasticsearch.elastic.co"
    assert i["tls"][0]["secretName"] == "elastic-co-wildcard"

    assert i["rules"][0]["host"] == "elasticsearch.elastic.co"
    assert i["rules"][0]["http"]["paths"][0]["path"] == "/"
    assert i["rules"][0]["http"]["paths"][0]["backend"]["service"]["name"] == uname
    assert (
        i["rules"][0]["http"]["paths"][0]["backend"]["service"]["port"]["number"]
        == 9200
    )
    assert i["rules"][1]["host"] == None
    assert i["rules"][1]["http"]["paths"][0]["path"] == "/"
    assert i["rules"][1]["http"]["paths"][0]["backend"]["service"]["name"] == uname
    assert (
        i["rules"][1]["http"]["paths"][0]["backend"]["service"]["port"]["number"]
        == 9200
    )
    assert i["rules"][1]["http"]["paths"][1]["path"] == "/mypath"
    assert i["rules"][1]["http"]["paths"][1]["backend"]["service"]["name"] == uname
    assert (
        i["rules"][1]["http"]["paths"][1]["backend"]["service"]["port"]["number"]
        == 8888
    )
    assert i["rules"][2]["host"] == "elasticsearch.hello.there"
    assert i["rules"][2]["http"]["paths"][0]["path"] == "/"
    assert i["rules"][2]["http"]["paths"][0]["backend"]["service"]["name"] == uname
    assert (
        i["rules"][2]["http"]["paths"][0]["backend"]["service"]["port"]["number"]
        == 9999
    )


def test_adding_a_deprecated_ingress_rule():
    config = """
ingress:
  enabled: true
  annotations:
    kubernetes.io/ingress.class: nginx
  path: /
  hosts:
    - elasticsearch.elastic.co
  tls:
  - secretName: elastic-co-wildcard
    hosts:
     - elasticsearch.elastic.co
"""

    r = helm_template(config)
    assert uname in r["ingress"]
    i = r["ingress"][uname]["spec"]
    assert i["tls"][0]["hosts"][0] == "elasticsearch.elastic.co"
    assert i["tls"][0]["secretName"] == "elastic-co-wildcard"

    assert i["rules"][0]["host"] == "elasticsearch.elastic.co"
    assert i["rules"][0]["http"]["paths"][0]["path"] == "/"
    assert i["rules"][0]["http"]["paths"][0]["backend"]["service"]["name"] == uname
    assert (
        i["rules"][0]["http"]["paths"][0]["backend"]["service"]["port"]["number"]
        == 9200
    )


def test_changing_the_protocol():
    config = """
protocol: https
"""
    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    assert "https://127.0.0.1:9200" in c["readinessProbe"]["exec"]["command"][-1]


def test_changing_the_cluster_health_status():
    config = """
clusterHealthCheckParams: 'wait_for_no_initializing_shards=true&timeout=60s'
"""
    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    assert (
        "/_cluster/health?wait_for_no_initializing_shards=true&timeout=60s"
        in c["readinessProbe"]["exec"]["command"][-1]
    )


def test_adding_in_es_config():
    config = """
esConfig:
  elasticsearch.yml: |
    key:
      nestedkey: value
    dot.notation: test

  log4j2.properties: |
    appender.rolling.name = rolling
"""
    r = helm_template(config)
    c = r["configmap"][uname + "-config"]["data"]

    assert "elasticsearch.yml" in c
    assert "log4j2.properties" in c

    assert "nestedkey: value" in c["elasticsearch.yml"]
    assert "dot.notation: test" in c["elasticsearch.yml"]

    assert "appender.rolling.name = rolling" in c["log4j2.properties"]

    s = r["statefulset"][uname]["spec"]["template"]["spec"]

    assert {
        "configMap": {"name": "elasticsearch-master-config"},
        "name": "esconfig",
    } in s["volumes"]
    assert {
        "mountPath": "/usr/share/elasticsearch/config/elasticsearch.yml",
        "name": "esconfig",
        "subPath": "elasticsearch.yml",
    } in s["containers"][0]["volumeMounts"]
    assert {
        "mountPath": "/usr/share/elasticsearch/config/log4j2.properties",
        "name": "esconfig",
        "subPath": "log4j2.properties",
    } in s["containers"][0]["volumeMounts"]

    assert (
        "configchecksum"
        in r["statefulset"][uname]["spec"]["template"]["metadata"]["annotations"]
    )


def test_adding_in_jvm_options():
    config = """
esJvmOptions:
    processors.options: |
        -XX:ActiveProcessorCount=3
"""
    r = helm_template(config)
    c = r["configmap"][uname + "-jvm-options"]["data"]

    assert "processors.options" in c

    assert "-XX:ActiveProcessorCount=3" in c["processors.options"]

    s = r["statefulset"][uname]["spec"]["template"]["spec"]

    assert {
        "configMap": {"name": "elasticsearch-master-jvm-options"},
        "name": "esjvmoptions",
    } in s["volumes"]
    assert {
        "mountPath": "/usr/share/elasticsearch/config/jvm.options.d/processors.options",
        "name": "esjvmoptions",
        "subPath": "processors.options",
    } in s["containers"][0]["volumeMounts"]

    assert (
        "configchecksum"
        in r["statefulset"][uname]["spec"]["template"]["metadata"]["annotations"]
    )


def test_dont_add_data_volume_when_persistance_is_disabled():
    config = """
persistence:
  enabled: false
"""
    r = helm_template(config)
    assert "volumeClaimTemplates" not in r["statefulset"][uname]["spec"]
    assert {
        "name": "elasticsearch-master",
        "mountPath": "/usr/share/elasticsearch/data",
    } not in r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0][
        "volumeMounts"
    ]


def test_priority_class_name():
    config = """
priorityClassName: ""
"""
    r = helm_template(config)
    spec = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert "priorityClassName" not in spec

    config = """
priorityClassName: "highest"
"""
    r = helm_template(config)
    priority_class_name = r["statefulset"][uname]["spec"]["template"]["spec"][
        "priorityClassName"
    ]
    assert priority_class_name == "highest"


def test_scheduler_name():
    r = helm_template("")
    spec = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert "schedulerName" not in spec

    config = """
schedulerName: "stork"
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["schedulerName"] == "stork"
    )


def test_disabling_non_headless_service():
    config = ""

    r = helm_template(config)

    assert uname in r["service"]

    config = """
service:
  enabled: false
"""

    r = helm_template(config)

    assert uname not in r["service"]


def test_enabling_service_publishNotReadyAddresses():
    config = """
    service:
      publishNotReadyAddresses: true
    """

    r = helm_template(config)

    assert r["service"][uname]["spec"]["publishNotReadyAddresses"] == True


def test_adding_a_nodePort():
    config = ""

    r = helm_template(config)

    assert "nodePort" not in r["service"][uname]["spec"]["ports"][0]

    config = """
    service:
      nodePort: 30001
    """

    r = helm_template(config)

    assert r["service"][uname]["spec"]["ports"][0]["nodePort"] == 30001


def test_adding_a_loadBalancerIP():
    config = ""

    r = helm_template(config)

    assert "loadBalancerIP" not in r["service"][uname]["spec"]

    config = """
    service:
      loadBalancerIP: 12.4.19.81
    """

    r = helm_template(config)

    assert r["service"][uname]["spec"]["loadBalancerIP"] == "12.4.19.81"


def test_adding_an_externalTrafficPolicy():
    config = ""

    r = helm_template(config)

    assert "externalTrafficPolicy" not in r["service"][uname]["spec"]

    config = """
    service:
      externalTrafficPolicy: Local
    """

    r = helm_template(config)

    assert r["service"][uname]["spec"]["externalTrafficPolicy"] == "Local"


def test_adding_a_label_on_non_headless_service():
    config = ""

    r = helm_template(config)

    assert "label1" not in r["service"][uname]["metadata"]["labels"]

    config = """
    service:
      labels:
        label1: value1
    """

    r = helm_template(config)

    assert r["service"][uname]["metadata"]["labels"]["label1"] == "value1"


def test_adding_a_label_on_headless_service():
    config = ""

    r = helm_template(config)

    assert "label1" not in r["service"][uname + "-headless"]["metadata"]["labels"]

    config = """
    service:
      labelsHeadless:
        label1: value1
    """

    r = helm_template(config)

    assert r["service"][uname + "-headless"]["metadata"]["labels"]["label1"] == "value1"


def test_adding_load_balancer_source_ranges():
    config = """
service:
  loadBalancerSourceRanges:
    - 0.0.0.0/0
    """
    r = helm_template(config)
    assert r["service"][uname]["spec"]["loadBalancerSourceRanges"][0] == "0.0.0.0/0"

    config = """
service:
  loadBalancerSourceRanges:
    - 192.168.0.0/24
    - 192.168.1.0/24
    """
    r = helm_template(config)
    ranges = r["service"][uname]["spec"]["loadBalancerSourceRanges"]
    assert ranges[0] == "192.168.0.0/24"
    assert ranges[1] == "192.168.1.0/24"


def test_lifecycle_hooks():
    config = ""
    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    assert "lifecycle" not in c

    config = """
    lifecycle:
      preStop:
        exec:
          command: ["/bin/bash","/preStop"]
    """
    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]

    assert c["lifecycle"]["preStop"]["exec"]["command"] == ["/bin/bash", "/preStop"]


def test_esMajorVersion_detect_default_version():
    config = ""

    r = helm_template(config)
    assert r["statefulset"][uname]["metadata"]["annotations"]["esMajorVersion"] == "8"


def test_esMajorVersion_default_to_8_if_not_elastic_image():
    config = """
    image: notElastic
    imageTag: 1.0.0
    """

    r = helm_template(config)
    assert r["statefulset"][uname]["metadata"]["annotations"]["esMajorVersion"] == "8"


def test_esMajorVersion_default_to_8_if_no_version_is_found():
    config = """
    imageTag: not_a_number
    """

    r = helm_template(config)
    assert r["statefulset"][uname]["metadata"]["annotations"]["esMajorVersion"] == "8"


def test_esMajorVersion_always_wins():
    config = """
    esMajorVersion: 7
    imageTag: 8.0.0
    """

    r = helm_template(config)
    assert r["statefulset"][uname]["metadata"]["annotations"]["esMajorVersion"] == "7"


def test_set_pod_security_context():
    config = ""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"][
            "fsGroup"
        ]
        == 1000
    )
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"][
            "runAsUser"
        ]
        == 1000
    )

    config = """
    podSecurityContext:
      fsGroup: 1001
      other: test
    """

    r = helm_template(config)

    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"][
            "fsGroup"
        ]
        == 1001
    )
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"]["other"]
        == "test"
    )


def test_fsGroup_backwards_compatability():
    config = """
    fsGroup: 1001
    """

    r = helm_template(config)

    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["securityContext"][
            "fsGroup"
        ]
        == 1001
    )


def test_set_container_security_context():
    config = ""

    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    assert c["securityContext"]["capabilities"]["drop"] == ["ALL"]
    assert c["securityContext"]["runAsNonRoot"] == True
    assert c["securityContext"]["runAsUser"] == 1000

    config = """
    securityContext:
      runAsUser: 1001
      other: test
    """

    r = helm_template(config)
    c = r["statefulset"][uname]["spec"]["template"]["spec"]["containers"][0]
    assert c["securityContext"]["capabilities"]["drop"] == ["ALL"]
    assert c["securityContext"]["runAsNonRoot"] == True
    assert c["securityContext"]["runAsUser"] == 1001
    assert c["securityContext"]["other"] == "test"


def test_adding_pod_labels():
    config = """
labels:
  app.kubernetes.io/name: elasticsearch
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["metadata"]["labels"]["app.kubernetes.io/name"]
        == "elasticsearch"
    )
    assert (
        r["statefulset"][uname]["spec"]["template"]["metadata"]["labels"][
            "app.kubernetes.io/name"
        ]
        == "elasticsearch"
    )


def test_keystore_enable():
    config = """
keystore:
  - secretName: test
    """

    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]

    assert {"name": "keystore", "emptyDir": {}} in s["volumes"]


def test_keystore_init_container():
    config = ""

    r = helm_template(config)
    i = r["statefulset"][uname]["spec"]["template"]["spec"]["initContainers"][-1]

    assert i["name"] != "keystore"

    config = """
keystore:
  - secretName: test
    """

    r = helm_template(config)
    i = r["statefulset"][uname]["spec"]["template"]["spec"]["initContainers"][-1]

    assert i["name"] == "keystore"


def test_keystore_init_container_image():
    config = """
image: customImage
imageTag: 6.2.4
imagePullPolicy: Never
keystore:
  - secretName: test
"""
    r = helm_template(config)
    i = r["statefulset"][uname]["spec"]["template"]["spec"]["initContainers"][-1]
    assert i["image"] == "customImage:6.2.4"
    assert i["imagePullPolicy"] == "Never"


def test_keystore_mount():
    config = """
keystore:
  - secretName: test
"""

    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert s["containers"][0]["volumeMounts"][-1] == {
        "mountPath": "/usr/share/elasticsearch/config/elasticsearch.keystore",
        "subPath": "elasticsearch.keystore",
        "name": "keystore",
    }


def test_keystore_init_volume_mounts():
    config = """
keystore:
  - secretName: test
  - secretName: test-with-custom-path
    items:
    - key: slack_url
      path: xpack.notification.slack.account.otheraccount.secure_url
"""
    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]
    assert s["initContainers"][-1]["volumeMounts"] == [
        {"mountPath": "/tmp/keystore", "name": "keystore"},
        {"mountPath": "/tmp/keystoreSecrets/test", "name": "keystore-test"},
        {
            "mountPath": "/tmp/keystoreSecrets/test-with-custom-path",
            "name": "keystore-test-with-custom-path",
        },
    ]


def test_keystore_volumes():
    config = """
keystore:
  - secretName: test
  - secretName: test-with-custom-path
    items:
    - key: slack_url
      path: xpack.notification.slack.account.otheraccount.secure_url
"""
    r = helm_template(config)
    s = r["statefulset"][uname]["spec"]["template"]["spec"]

    assert {"name": "keystore-test", "secret": {"secretName": "test"}} in s["volumes"]

    assert {
        "name": "keystore-test-with-custom-path",
        "secret": {
            "secretName": "test-with-custom-path",
            "items": [
                {
                    "key": "slack_url",
                    "path": "xpack.notification.slack.account.otheraccount.secure_url",
                }
            ],
        },
    } in s["volumes"]


def test_pod_security_policy():
    ## Make sure the default config is not creating any resources
    config = ""
    resources = ("role", "rolebinding", "serviceaccount", "podsecuritypolicy")
    r = helm_template(config)
    for resource in resources:
        assert resource not in r
    assert (
        "serviceAccountName" not in r["statefulset"][uname]["spec"]["template"]["spec"]
    )

    ## Make sure all the resources are created with default values
    config = """
rbac:
  create: true
  serviceAccountName: ""

podSecurityPolicy:
  create: true
  name: ""
"""
    r = helm_template(config)
    for resource in resources:
        assert resource in r
    assert r["role"][uname]["rules"][0] == {
        "apiGroups": ["extensions"],
        "verbs": ["use"],
        "resources": ["podsecuritypolicies"],
        "resourceNames": [uname],
    }
    assert r["rolebinding"][uname]["subjects"] == [
        {"kind": "ServiceAccount", "namespace": "default", "name": uname}
    ]
    assert r["rolebinding"][uname]["roleRef"] == {
        "apiGroup": "rbac.authorization.k8s.io",
        "kind": "Role",
        "name": uname,
    }
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["serviceAccountName"]
        == uname
    )
    psp_spec = r["podsecuritypolicy"][uname]["spec"]
    assert psp_spec["privileged"] is True


def test_external_pod_security_policy():
    ## Make sure we can use an externally defined pod security policy
    config = """
rbac:
  create: true
  serviceAccountName: ""

podSecurityPolicy:
  create: false
  name: "customPodSecurityPolicy"
"""
    resources = ("role", "rolebinding")
    r = helm_template(config)
    for resource in resources:
        assert resource in r

    assert r["role"][uname]["rules"][0] == {
        "apiGroups": ["extensions"],
        "verbs": ["use"],
        "resources": ["podsecuritypolicies"],
        "resourceNames": ["customPodSecurityPolicy"],
    }


def test_external_service_account():
    ## Make sure we can use an externally defined service account
    config = """
rbac:
  create: false
  serviceAccountName: "customServiceAccountName"

podSecurityPolicy:
  create: false
  name: ""
"""
    resources = ("role", "rolebinding", "serviceaccount")
    r = helm_template(config)

    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"]["serviceAccountName"]
        == "customServiceAccountName"
    )
    # When referencing an external service account we do not want any resources to be created.
    for resource in resources:
        assert resource not in r


def test_name_override():
    ## Make sure we can use a name override
    config = """
nameOverride: "customName"
"""
    r = helm_template(config)

    assert "customName-master" in r["statefulset"]
    assert "customName-master" in r["service"]


def test_full_name_override():
    ## Make sure we can use a full name override
    config = """
fullnameOverride: "customfullName"
"""
    r = helm_template(config)

    assert "customfullName" in r["statefulset"]
    assert "customfullName" in r["service"]


def test_initial_master_nodes_when_using_full_name_override():
    config = """
fullnameOverride: "customfullName"
"""
    r = helm_template(config)
    env = r["statefulset"]["customfullName"]["spec"]["template"]["spec"]["containers"][
        0
    ]["env"]
    assert {
        "name": "cluster.initial_master_nodes",
        "value": "customfullName-0," + "customfullName-1," + "customfullName-2,",
    } in env


def test_hostaliases():
    config = """
hostAliases:
- ip: "127.0.0.1"
  hostnames:
  - "foo.local"
  - "bar.local"
"""
    r = helm_template(config)
    hostAliases = r["statefulset"][uname]["spec"]["template"]["spec"]["hostAliases"]
    assert {"ip": "127.0.0.1", "hostnames": ["foo.local", "bar.local"]} in hostAliases


def test_network_policy():
    config = """
networkPolicy:
  http:
    enabled: true
    explicitNamespacesSelector:
      # Accept from namespaces with all those different rules (from whitelisted Pods)
      matchLabels:
        role: frontend-http
      matchExpressions:
        - {key: role, operator: In, values: [frontend-http]}
    additionalRules:
      - podSelector:
          matchLabels:
            role: frontend-http
      - podSelector:
          matchExpressions:
            - key: role
              operator: In
              values:
                - frontend-http
  transport:
    enabled: true
    allowExternal: true
    explicitNamespacesSelector:
      matchLabels:
        role: frontend-transport
      matchExpressions:
        - {key: role, operator: In, values: [frontend-transport]}
    additionalRules:
      - podSelector:
          matchLabels:
            role: frontend-transport
      - podSelector:
          matchExpressions:
            - key: role
              operator: In
              values:
                - frontend-transport

"""
    r = helm_template(config)
    ingress = r["networkpolicy"][uname]["spec"]["ingress"]
    pod_selector = r["networkpolicy"][uname]["spec"]["podSelector"]
    http = ingress[0]
    transport = ingress[1]
    assert http["from"] == [
        {
            "podSelector": {
                "matchLabels": {"elasticsearch-master-http-client": "true"}
            },
            "namespaceSelector": {
                "matchExpressions": [
                    {"key": "role", "operator": "In", "values": ["frontend-http"]}
                ],
                "matchLabels": {"role": "frontend-http"},
            },
        },
        {"podSelector": {"matchLabels": {"role": "frontend-http"}}},
        {
            "podSelector": {
                "matchExpressions": [
                    {"key": "role", "operator": "In", "values": ["frontend-http"]}
                ]
            }
        },
    ]
    assert http["ports"][0]["port"] == 9200
    assert transport["from"] == [
        {
            "podSelector": {
                "matchLabels": {"elasticsearch-master-transport-client": "true"}
            },
            "namespaceSelector": {
                "matchExpressions": [
                    {"key": "role", "operator": "In", "values": ["frontend-transport"]}
                ],
                "matchLabels": {"role": "frontend-transport"},
            },
        },
        {"podSelector": {"matchLabels": {"role": "frontend-transport"}}},
        {
            "podSelector": {
                "matchExpressions": [
                    {"key": "role", "operator": "In", "values": ["frontend-transport"]}
                ]
            }
        },
        {"podSelector": {"matchLabels": {"app": "elasticsearch-master"}}},
    ]
    assert transport["ports"][0]["port"] == 9300
    assert pod_selector == {
        "matchLabels": {
            "app": "elasticsearch-master",
        }
    }


def test_default_automount_sa_token():
    config = """
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"][
            "automountServiceAccountToken"
        ]
        == True
    )


def test_disable_automount_sa_token():
    config = """
rbac:
  automountToken: false
"""
    r = helm_template(config)
    assert (
        r["statefulset"][uname]["spec"]["template"]["spec"][
            "automountServiceAccountToken"
        ]
        == False
    )
