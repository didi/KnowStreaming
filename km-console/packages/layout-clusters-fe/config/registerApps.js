import * as singleSpa from 'single-spa';

const customProps = {
  env: {
    NODE_ENV: process.env.NODE_ENV,
    BUSINESS_VERSION: process.env.BUSINESS_VERSION,
  },
};

function fetchManifest(url, publicPath) {
  return fetch(url)
    .then((res) => {
      return res.text();
    })
    .then((data) => {
      if (data) {
        const manifest = data.match(/<meta name="manifest" content="([\w|\d|-]+.json)">/);
        let result = '';
        if (publicPath && manifest) {
          result = `${publicPath}${manifest[1]}?q=${new Date().getTime()}`;
        }
        return result;
      }
    });
}

function prefix(location, ident, matchPath) {
  if (matchPath && Object.prototype.toString.call(matchPath) === '[object Function]') {
    return matchPath(location);
  }
  if (location.href === `${location.origin}/${ident}`) {
    return true;
  }
  return location.href.indexOf(`${location.origin}/${ident}`) !== -1;
}

function getStylesheetLink(ident) {
  return document.getElementById(`${ident}-stylesheet`);
}

function createStylesheetLink(ident, path) {
  const headEle = document.getElementsByTagName('head')[0];
  const linkEle = document.createElement('link');
  linkEle.id = `${ident}-stylesheet`;
  linkEle.rel = 'stylesheet';
  // linkEle.href = systemConf[process.env.NODE_ENV].css;
  linkEle.href = path;
  headEle.appendChild(linkEle);
}

function removeStylesheetLink(ident) {
  const linkEle = getStylesheetLink(ident);
  if (linkEle) linkEle.remove();
}

async function getPathBySuffix(systemConf, jsonData, suffix) {
  let targetPath = '';
  _.forEach(Object.values(jsonData.assetsByChunkName), (assetsArr) => {
    if (typeof assetsArr === 'string' && assetsArr.indexOf(systemConf.ident) === 0 && _.endsWith(assetsArr, suffix)) {
      targetPath = assetsArr;
    }
    if (Array.isArray(assetsArr)) {
      targetPath = assetsArr.find((assetStr) => {
        return assetStr.indexOf(systemConf.ident) === 0 && _.endsWith(assetStr, suffix);
      });
      if (targetPath) {
        return false;
      }
    }
  });
  return `${systemConf[process.env.NODE_ENV].publicPath}${targetPath}`;
}

async function loadAssertsFileBySuffix(systemConf, jsonData, suffix) {
  const chunks = Object.values(jsonData.assetsByChunkName);
  const isJS = /js$/.test(suffix);
  await Promise.all(
    chunks.map(async (assetsArr) => {
      let targetPath = '';
      if (typeof assetsArr === 'string') {
        targetPath = assetsArr;
      } else if (Array.isArray(assetsArr)) {
        targetPath = assetsArr.find((assetStr) => {
          if (isJS) {
            return assetStr.indexOf(systemConf.ident) < 0 && _.endsWith(assetStr, suffix);
          } else {
            return _.endsWith(assetStr, suffix);
          }
        });
      }
      if (!targetPath) return Promise.resolve();
      if (isJS) {
        return System.import(`${systemConf[process.env.NODE_ENV].publicPath}${targetPath}`);
      } else {
        return createStylesheetLink(systemConf.ident, `${systemConf[process.env.NODE_ENV].publicPath}${targetPath}`);
      }
    })
  );
}

export default function registerApps(systemsConfig, props = {}, mountCbk) {
  systemsConfig.forEach(async (systemsConfItem) => {
    const { ident, matchPath } = systemsConfItem;
    const sysUrl = systemsConfItem[process.env.NODE_ENV].index;

    singleSpa.registerApplication(
      ident,
      async () => {
        let manifestUrl = `${sysUrl}?q=${new Date().getTime()}`;

        // html 作为入口文件
        if (/.+html$/.test(sysUrl)) {
          manifestUrl = await fetchManifest(sysUrl, systemsConfItem[process.env.NODE_ENV].publicPath);
        }

        const lifecyclesFile = await fetch(manifestUrl).then((res) => res.json());
        let lifecycles = {};
        if (lifecyclesFile) {
          await loadAssertsFileBySuffix(systemsConfItem, lifecyclesFile, '.js');
          const jsPath = await getPathBySuffix(systemsConfItem, lifecyclesFile, '.js');
          lifecycles = await System.import(jsPath);
        } else {
          lifecycles = lifecyclesFile;
        }
        const { mount, unmount } = lifecycles;
        mount.unshift(async () => {
          if (lifecyclesFile) {
            await loadAssertsFileBySuffix(systemsConfItem, lifecyclesFile, '.css');
            // const cssPath = await getPathBySuffix(systemsConfItem, lifecyclesFile, '.css');
            // createStylesheetLink(ident, cssPath);
          }
          return Promise.resolve();
        });

        if (mountCbk) {
          mount.unshift(async () => {
            mountCbk();
            return Promise.resolve();
          });
        }
        unmount.unshift(() => {
          removeStylesheetLink(ident);
          return Promise.resolve();
        });
        return lifecycles;
      },
      (location) => prefix(location, ident, matchPath),
      {
        ...customProps,
        ...props,
      }
    );
  });
  singleSpa.start();
}
