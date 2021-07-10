interface IData {
  startTime: number;
  value: string;
  time: number;
}

export const setValueToLocalStorage = (key: string , value: string, time: number = 1 * 60 * 60 * 1000) => {
  const data = {
    startTime: (new Date()).getTime(),
    value,
    time,
  } as IData;
  localStorage.setItem(key, JSON.stringify(data));
};

export const getValueFromLocalStorage = (key: string) => {
  let result = null;
  const data = localStorage.getItem(key);
  if (data) {
    try {
      result = JSON.parse(data);
      const { startTime, value, time } = result;
      const isTimeout = (new Date()).getTime() - startTime >= time;
      if (isTimeout) {
        deleteValueFromLocalStorage(key);
      }
      result = isTimeout ? null : value;
    } catch (err) {
      // do nothing
    }
  }
  return result;
};

export const deleteValueFromLocalStorage = (key: string) => {
  localStorage.removeItem(key);
};
