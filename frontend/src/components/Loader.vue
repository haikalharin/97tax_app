<template>
  <div  class="backdrop" v-if="isLoaderOn" ><img class="loader-gif" src="@/assets/loader.gif"></div>
</template>
<script>
  export default {
    props:{
        isLoaderOn:{
            type:Boolean,
            value:false
        }
    }
  }

</script>
<style scoped>

.backdrop{
    position: fixed;
    top: 0;
    bottom: 0;
    right: 0;
    left: 0;
    background: black;
    opacity: 0.6;
    z-index: 1;
    align-items: center;
    display: flex;
    justify-content: space-evenly;
}
.loader-gif{
  z-index: 2;
}
</style>