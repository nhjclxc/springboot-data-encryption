const { defineConfig } = require('@vue/cli-service')


const port = process.env.port || process.env.npm_config_port || 80 // 端口

console.log('process.env.VUE_APP_BASE_URL', process.env.VUE_APP_BASE_URL)

module.exports = defineConfig({
  transpileDependencies: true,
  // 关闭eslint校验
  lintOnSave: false,
  // 配置代理
  devServer: {
    host: '0.0.0.0',
    port: port,
    open: true,
    proxy: {
      // https://cli.vuejs.org/config/#devserver-proxy
      [process.env.VUE_APP_BASE_API]: {
        target: process.env.VUE_APP_BASE_URL,
        changeOrigin: true,
        pathRewrite: {
          ['^' + process.env.VUE_APP_BASE_API]: ''
        }
      }
    }
  }
})
