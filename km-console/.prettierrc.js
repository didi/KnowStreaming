module.exports = {
  "printWidth": 140, // 指定代码长度，超出换行
  "tabWidth": 2, // tab 键的宽度
  "semi": true, // 结尾加上分号
  "trailingComma": "es5", // 确保对象的最后一个属性后有逗号
  singleQuote: true,
  "bracketSpacing": true, // 大括号有空格 { name: 'rose' }
  "insertPragma": false, // 是否在格式化的文件顶部插入Pragma标记，以表明该文件被prettier格式化过了
  "htmlWhitespaceSensitivity": "ignore", // html文件的空格敏感度，控制空格是否影响布局
}