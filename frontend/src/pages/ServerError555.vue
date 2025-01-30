<template>
  <section class="centered">
    <h1>555 Server Error</h1>
    <div>
      <span class="message">Oops. It appears there was an unexpected error. Sorry.</span>
    </div>
  </section>
</template>

<style>
@import url('https://fonts.googleapis.com/css?family=Lato|Roboto+Slab');

.google-language-selection__panel {
    display: none;
}

* {
  position: relative;
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.centered {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

h1 {
  margin-bottom: 50px;
  font-family: 'Lato', sans-serif;
  font-size: 50px;
}

.message {
  display: inline-block;
  line-height: 1.2;
  transition: line-height .2s, width .2s;
  overflow: hidden;
}
</style>
