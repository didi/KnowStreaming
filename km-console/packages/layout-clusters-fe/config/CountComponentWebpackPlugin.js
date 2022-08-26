const chalk = require('chalk');
const fs = require('fs');
// const toExcel = require('to-excel').toExcel;

class CountComponentPlugin {
  constructor(opts = {}) {
    this.opts = {
      startCount: true, // 是否开启统计
      isExportExcel: false, // 是否生成excel
      pathname: '', // 文件路径
      ...opts,
    };
    this.total = {
      len: 0,
      components: {},
    };
  }
  sort(obj) {
    this.total.components = Object.fromEntries(Object.entries(obj).sort(([, a], [, b]) => b - a));
  }

  // 生成excel 文件
  // toExcel() {
  //   const arr = [];
  //   Object.keys(this.total.components).forEach((key, index) => {
  //     const value = this.total.components[key];
  //     const data = {
  //       id: index + 1,
  //       component: key,
  //       count: value,
  //     };
  //     arr.push(data);
  //   });

  //   const headers = [
  //     { label: '名次', field: 'id' },
  //     { label: '组件', field: 'component' },
  //     { label: '次数', field: 'count' },
  //   ];
  //   const content = toExcel.exportXLS(headers, arr, 'filename');
  //   fs.writeFileSync('filename.xls', content);
  // }

  toLog() {
    this.sort(this.total.components);
    Object.keys(this.total.components).forEach((key) => {
      const value = this.total.components[key];
      const per = Number((value / this.total.len).toPrecision(3)) * 100;
      console.log(`\n${chalk.blue(key)} 组件引用次数 ${chalk.green(value)} 引用率 ${chalk.redBright(per)}%`);
    });
    console.log(`\n组件${chalk.blue('总共')}引用次数 ${chalk.green(this.total.len)}`);
  }
  apply(compiler) {
    const handler = (_compilation, { normalModuleFactory }) => {
      normalModuleFactory.hooks.parser.for('javascript/auto').tap('count-component-plugin', (parser) => {
        parser.hooks.importSpecifier.tap('count-component-plugin', (_statement, source, _exportName, identifierName) => {
          if (source.includes(this.opts.pathname)) {
            this.total.len = this.total.len + 1;
            const key = identifierName;
            this.total.components[key] = this.total.components[key] ? this.total.components[key] + 1 : 1;
          }
        });
        parser.hooks.program.tap('count-component-plugin', (ast) => {
          // console.log('+++++++', ast);
        });
      });
    };
    const done = () => {
      if (!this.opts.startCount) {
        return;
      }
      this.sort(this.total.components);
      if (this.opts.isExportExcel) {
        this.toLog();
      } else {
        this.toLog();
      }
    };
    compiler.hooks.compilation.tap('count-component-plugin', handler);
    compiler.hooks.done.tap('count-component-plugin-done', done);
  }
}
module.exports = CountComponentPlugin;
