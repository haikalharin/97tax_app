<template>
  <page>
    <div class="top-hero pt-5 pb-4">
      <div>
        <div class="text-center">
          <h1>STATE PAYMENT PLANS</h1>
        </div>
      </div>
      <div>
        <div class="text-center">
          <span class="sub-head-text">
            Payment Plan Applications by State
          </span>
        </div>
      </div>
    </div>
    <div class="states-div">
      <div class="mb-4 mt-3 text-center">
        <h3 class="font-weight-bold">Select Your State to Get Started</h3>
      </div>
      <div style="width: 100%; margin: auto">
        <div class="text-center">
          <!-- web for used to add coords on image https://www.image-map.net/-->
          <img
            id="mapimg"
            src="@/assets/states-map.png"
            style="width: 100%; max-width: 586px"
            usemap="#image-map"
            @mousemove="mouseMove"
            @click="onClickMap"
          />
        </div>
      </div>
    </div>
  </page>
</template>

<script>
export default {
  data() {
    return {
      selectedState: "",
      statesMap: [
        {
          shape: "poly",
          coords: "15,95,52,106,42,143,93,225,82,242,59,240,26,201,6,128",
          title: "CALIFORNIA",
          alt: "CALIFORNIA",
          state: "california",
          coordsArray: [
            [15, 95],
            [52, 106],
            [42, 143],
            [93, 225],
            [82, 242],
            [59, 240],
            [26, 201],
            [6, 128],
          ],
        },
        {
          shape: "poly",
          coords: "357,127,383,127,391,173,377,202,361,180,347,164",
          title: "ILLINOIS",
          alt: "ILLINOIS",
          state: "illinois",
          coordsArray: [
            [357, 127],
            [383, 127],
            [391, 173],
            [377, 202],
            [361, 180],
            [347, 164],
          ],
        },
        {
          shape: "poly",
          coords: "419,226,429,263,432,285,469,284,473,259,444,226",
          title: "GEORGIA",
          alt: "GEORGIA",
          state: "georgia",
          coordsArray: [
            [419, 226],
            [429, 263],
            [432, 285],
            [469, 284],
            [473, 259],
            [444, 226],
          ],
        },
        {
          shape: "poly",
          coords: "519,119,517,134,523,152,533,135,530,123",
          title: "NEW JERSEY",
          alt: "NEW JERSEY",
          state: "new-jersey",
          coordsArray: [
            [519, 119],
            [517, 134],
            [523, 152],
            [533, 135],
            [530, 123],
          ],
        },
        {
          shape: "poly",
          coords: "413,80,399,99,401,126,422,125,434,111",
          title: "MICHIGAN",
          alt: "MICHIGAN",
          state: "michigan",
          coordsArray: [
            [413, 80],
            [399, 99],
            [401, 126],
            [422, 125],
            [434, 111],
          ],
        },
      ],
    };
  },
  methods: {
    goToStatePlan(state) {
      this.$router.push("/" + state + "-payment-plan");
    },
    inside(point, vs) {
      // ray-casting algorithm based on
      // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html/pnpoly.html

      var x = point[0],
        y = point[1];

      var inside = false;
      for (var i = 0, j = vs.length - 1; i < vs.length; j = i++) {
        var xi = vs[i][0],
          yi = vs[i][1];
        var xj = vs[j][0],
          yj = vs[j][1];

        var intersect =
          yi > y != yj > y && x < ((xj - xi) * (y - yi)) / (yj - yi) + xi;
        if (intersect) inside = !inside;
      }

      return inside;
    },
    mouseMove(e) {
      let orgW = 586;
      let mapimg = document.getElementById("mapimg");
      let w = mapimg.offsetWidth;
      let rate = w / orgW;
      let pos = [e.offsetX, e.offsetY];

      for (let state of this.statesMap) {
        let convertedArray = this.covertByRate(state.coordsArray, rate);
        let isInside = this.inside(pos, convertedArray);

        if (isInside) {
          mapimg.style.cursor = "pointer";
          this.selectedState = state.state;
          break;
        } else {
          mapimg.style.cursor = "auto";
          this.selectedState = "";
        }
      }
    },
    covertByRate(arr, rate) {
      if (rate == 1) return arr;

      let result = [];
      for (let i = 0; i < arr.length; i++) {
        result[i] = [arr[i][0] * rate, arr[i][1] * rate];
      }

      return result;
    },
    onClickMap() {
      this.$router.push("/" + this.selectedState + "-payment-plan");
    },
  },
};
</script>

<style scoped>
.states-div {
  margin-bottom: 20px;
}

.state-card {
  height: 14vmax;
  background-color: rgb(52, 202, 0);
  margin-top: 1vmax;
  color: #fff;
}

.detail-img {
  height: 11vmax;
}
.top-hero {
  background-color: #0094be;
  color: #fff;
}
.detailed-text {
  font-size: 1.4vw;
}

.sub-head-text {
  font-size: larger;
  font-weight: 800;
}

h1,
h2 {
  font-weight: 800 !important;
}

.card-body {
  cursor: pointer;
}
.card-heading {
  font-weight: 700 !important;
}

@media (max-width: 1330px) {
  .card-heading {
    font-size: 24px;
  }

  .detailed-text {
    font-size: medium;
  }
}

@media (max-width: 928px) {
  .card-heading {
    font-size: 18px;
  }
  .detailed-text {
    font-size: medium;
  }
  .state-card {
    height: auto !important;
  }
}
</style>
