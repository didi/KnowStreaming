const MultiPageSteps = {
  key: 'MultiPage',
  steps: [
    {
      target: '.cluster-header-card',
      title: 'Cluster 总数',
      content: '这里展示了集群的数量和运行状态',
      disableBeacon: true,
      placement: 'bottom-start' as const,
    },
    {
      target: '.header-filter-bottom',
      title: '版本选择、健康分',
      content: '这里展示了版本、健康分的统计信息，并可以进行筛选',
      placement: 'bottom-start' as const,
    },
    {
      target: '.multi-cluster-list-item:first-child',
      title: '集群卡片',
      content: '这里展示了每个集群状态、指标等综合信息，点击可以 进入单集群管理页面',
      placement: 'bottom-start' as const,
    },
  ],
};

const ClusterDetailSteps = {
  key: 'ClusterDetail',
  steps: [
    {
      target: '.single-cluster-detail .left-sider',
      title: '集群概览',
      content: '这里展示了集群的整体健康状态和统计信息',
      disableBeacon: true,
      placement: 'right-start' as const,
    },
    {
      target: '.single-cluster-detail .cluster-detail .left-sider .healthy-state-status .icon',
      title: '设置健康度',
      content: '点击这里可以设置集群的健康检查项、权重及规则',
      placement: 'right-start' as const,
      spotlightPadding: 5,
      styles: {
        spotlight: {
          borderRadius: 6,
        },
      },
    },
    {
      target: '.single-cluster-detail .cluster-detail .left-sider .healthy-state-btn',
      title: '查看健康状态详情',
      content: '点击这里可以查看集群的健康状态的检查结果',
      placement: 'right-start' as const,
      spotlightPadding: 6,
      styles: {
        spotlight: {
          borderRadius: 6,
        },
      },
    },
    {
      target: '.single-cluster-detail .cluster-detail .left-sider .title .edit-icon-box',
      title: '编辑集群信息',
      content: '点击这里可以查看集群配置信息，并且可以对信息进行编辑',
      placement: 'right-start' as const,
      spotlightPadding: 1,
      styles: {
        spotlight: {
          borderRadius: 6,
        },
      },
    },
    {
      target: '.single-cluster-detail .ks-chart-container-header .header-right .dcloud-btn',
      title: '指标筛选',
      content: '点击这里可以对展示的图表进行筛选',
      placement: 'left-start' as const,
    },
    {
      target: '.single-cluster-detail .cluster-detail .change-log-panel',
      title: '历史变更记录',
      content: '这里展示了配置变更的历史记录',
      placement: 'left-start' as const,
    },
  ],
};

export { MultiPageSteps, ClusterDetailSteps };
