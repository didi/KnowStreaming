interface IMap {
  [key: string]: string;
}

export default () => {
  const Url = {
    hash: {} as IMap,
    search: {} as IMap,
  } as {
    hash: IMap;
    search: IMap;
    [key: string]: IMap;
  };

  window.location.hash.slice(1).split('&').map(str => {
    const kv = str.split('=');
    Url.hash[kv[0]] = kv[1];
  });

  window.location.search.slice(1).split('&').map(str => {
    const kv = str.split('=');
    Url.search[kv[0]] = kv[1];
  });
  return Url;
};
