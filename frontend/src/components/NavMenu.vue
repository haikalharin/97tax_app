<template>
  <div class="nav-menu">
    <span class="nav-item">Frequently Asked Questions</span>
    <span class="nav-item">Case Tracker</span>
    <span class="nav-item">Contact Us</span>
  </div>
</template>

<style>
  .nav-menu {
    display: flex;
    align-items: center;
    margin-top: 10px;
    font-family: Raleway, sans-serif;
    font-size: 20px;
    font-weight: 800;
    letter-spacing: 0px;
    color: #231f20;
    justify-content: flex-end;
  }
  span.nav-item {
    border-right: 2px solid #321f20;
    padding-right: 30px;
    padding-left: 30px;
  }
  .nav-menu span.nav-item:last-child {
    border-right: 0;
  }
  span.nav-item:hover {
    color: #2896c5;
  }
</style>
