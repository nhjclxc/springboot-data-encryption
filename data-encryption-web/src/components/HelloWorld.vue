<template>

  <div class="hello">

    哈哈哈
<!--    <el-button>我是 ElButton</el-button>-->
  </div>

</template>

<script>

import request from '@/request/request'
// import { ElButton } from 'element-plus'

export default {
  name: 'HelloWorld',
  // components: { ElButton },
  data() {
    return {}
  },
  created() {

    request({
      method: 'get',
      url: 'http://localhost:9090/encryption/get',
      params: {
        'str': '2024-05-01',
        'num': 111
      }
    }).then((res) => {
      console.log('接口响应解密数据 ', res)
    })
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
