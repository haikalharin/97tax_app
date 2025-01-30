<template>
  <div v-html="pageContent.htmlContent"></div>
</template>

<script>
  export default {
    name: 'PageContent',
    props: ['pageContent'],
    data () {
      return {
        pageContent: {
          htmlContent: ""
        }
      }
    }
  }
</script>
