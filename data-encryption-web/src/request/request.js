import axios from 'axios'
// 获取加密工具
import {sm4} from '@/utils/sm4Util'

axios.defaults.headers['Content-Type'] = 'application/json;charset=utf-8'

// 创建axios实例
const service = axios.create({
    // axios中请求配置有baseURL选项，表示请求URL公共部分
    baseURL: process.env.VUE_APP_BASE_API,
    // 超时时间
    timeout: 10000
})


/**
 * request拦截器
 */
service.interceptors.request.use(config => {


    // 检查是否要加密请求
    const flag = process.env.VUE_APP_ENCRYPT_ENABLE
    console.log('flag', flag)
    if (flag) {
        config.headers[process.env.VUE_APP_ENCRYPT_FLAG] = flag

        if (config.headers['Content-Type'].indexOf('application/x-www-form-urlencoded') > -1) {
            console.log('1')
            //表单类型加密
            if (config.data) {
                let newData = config.data
                for (let d in newData) {
                    newData[d] = sm4.encrypt(newData[d])
                }
                config.data = newData
            }
        } else {
            console.log('2')
            // 非上传文件类加密
            if (config.headers['Content-Type'].indexOf('multipart/form-data') < 0) {
                console.log(3)
                //json类型加密
                config.data = sm4.encrypt(JSON.stringify(config.data))
            } else {
                console.log(4)
                if (config.data instanceof FormData) {
                    console.log(5)
                    if (config.data.forEach) {
                        console.log(6)
                        config.data.forEach((v, n) => {
                            console.log(7)
                            if (!(v instanceof File)) {
                                console.log(8)
                                config.data.set(n, sm4.encrypt(v))
                            }
                        })
                    } else {
                        console.log(9)
                        /**
                         * IOS在的FormData类型缺少forEach方法，所以用get进行获取，文件key值为file，仅支持单文件
                         */
                        let file = config.data.get('file')
                        if (!(file instanceof File)) {
                            config.data.set('file', sm4.encrypt(file))
                        }
                    }
                }
            }
        }
        //请求头入参加密
        if (config.params) {
            console.log(10)
            let newParams = {}
            for (let k in config.params) {
                console.log(11)
                let value = config.params[k]
                newParams[k] = sm4.encrypt(value)
            }
            config.params = newParams
        }
    }

    return config
}, error => {
    console.log(error)
    Promise.reject(error)
})


/**
 * 响应拦截器
 */
service.interceptors.response.use(res => {
        // 未设置状态码则默认成功状态
        const code = res.data.code || 200;
        // 获取错误信息
        const msg = res.data.msg

        // 二进制数据则直接返回
        if (res.request.responseType === 'blob' || res.request.responseType === 'arraybuffer' || res.headers['content-type'] === 'application/octet-stream') {
            return res.data
        }
        if (code >= 200 && code < 300) {
            console.log('接口响应数据', res.data)

            let flag = res.headers[process.env.VUE_APP_ENCRYPT_FLAG.toLocaleLowerCase()]
            if (flag) {
                // 解密
                res.data = JSON.parse(sm4.decrypt(res.data))
            }
            return res.data
        } else if (code === 500) {
            console.log('msg', msg)
            return Promise.reject(new Error(msg))
        } else if (code === 601) {
            console.log('msg', msg)
            return Promise.reject('error')
        } else if (code !== 200) {
            console.log('msg', msg)
            return Promise.reject('error')
        }
    },
    error => {
        console.log('error' + error)
        return Promise.reject(error)
    }
)

export default service
