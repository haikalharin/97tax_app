<template>
  <b-modal :id="id" :title="title">
    <p class="my-4">{{content}}</p>
  </b-modal>
</template>

<script>
  export default {
    name: 'Modal',
    props: ['id', 'title', 'content']

  }

</script>
