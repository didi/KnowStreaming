export const getRandomStr = (length?: number) => {
  const NUM_list = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
  const LOW_LETTERS_LIST = [
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'j',
    'k',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'u',
    'v',
    'w',
    'x',
    'y',
    'z',
  ];
  const CAP_LETTERS_LIST = LOW_LETTERS_LIST.map((v) => v.toUpperCase());
  const SPECIAL_LIST = [
    '!',
    '"',
    '#',
    '$',
    '%',
    '&',
    "'",
    '(',
    ')',
    '*',
    '+',
    '-',
    '.',
    '/',
    ':',
    ';',
    '<',
    '=',
    '>',
    '?',
    '@',
    '[',
    '\\',
    ']',
    '^',
    '_',
    '`',
    '{',
    '|',
    '}',
    '~',
  ];
  const ALL_LIST = [...NUM_list, ...LOW_LETTERS_LIST, ...CAP_LETTERS_LIST];
  const randomNum = (Math.random() * 128) | 0;
  const randomKeys = new Array(length ?? randomNum).fill('');

  for (let i = 0; i < randomKeys.length; i++) {
    // ALL_LIST 随机字符
    const index = (Math.random() * ALL_LIST.length) | 0;
    randomKeys[i] = ALL_LIST[index - 1];
  }
  return randomKeys.join('');
};
export const timeFormat = function formatDuring(mss: number) {
  var days = Math.floor(mss / (1000 * 60 * 60 * 24));
  var hours = Math.floor((mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  var minutes = Math.floor((mss % (1000 * 60 * 60)) / (1000 * 60));
  var seconds = (mss % (1000 * 60)) / 1000;
  var parts = [
    { v: days, unit: "天" },
    { v: hours, unit: "小时" },
    { v: minutes, unit: "分钟" },
    { v: seconds, unit: "秒" },
  ]
  return parts.filter(o => o.v > 0).map((o: any) => `${o.v}${o.unit}`).join();
}
