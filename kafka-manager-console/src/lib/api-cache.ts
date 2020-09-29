class ApiCache {
  public apiCacheMap = new Map();

  public setCacheMap = (key: string, data: any, timeout: number = 1 * 60 * 60 * 1000) => {
    this.apiCacheMap.set(key, {
      data,
      timeout,
      startTime: (new Date()).getTime(),
    });
  }

  public getDataFromCache = (key: string) => {
    const cacheData = this.apiCacheMap.get(key);

    if (!cacheData) return null;

    const { data, timeout, startTime } = cacheData;
    const currentTime = (new Date()).getTime();

    if ((currentTime - startTime) > timeout) {
      this.deleteDataFromCache(key);
      return null;
    }
    return data;
  }

  public deleteDataFromCache = (key: string) => {
    return this.apiCacheMap.delete(key);
  }
}

export const apiCache = new ApiCache();
