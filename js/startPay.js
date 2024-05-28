const priceList = [10, 20, 50, 100, 200, 500, 1000];

let price;

function selectProduct(itemIndex) {
  // reset all item style
  let selectItems = document.getElementsByClassName("per-act");
  if (selectItems.length > 0) {
    for (let i = 0; i < selectItems.length; i++) {
      selectItems[i].setAttribute("class", "per perb");
    }
  }

  // set current selected item style
  let productList = document.getElementById("product_list");
  productList.children[itemIndex].setAttribute("class", "per per-act");

  // set button style
  let btn = document.getElementById("buy");
  btn.setAttribute("class", "c");
  btn.innerText = "Recharge: " + priceList[itemIndex];
  price = priceList[itemIndex];
}

function startPay() {
  window.handleinitDataCallback = function () {
    window.location.href = window.location.origin;
  };
  if (!price) {
    return;
  }

  let loading = weui.loading("loading", {});
  window
    .fetch(baseUrl + "/create/order", {
      method: "post",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        title: "diamond_" + price,
        amount: price + "",
      }),
    })
    .then((res) => {
      res
        .text()
        .then((rawRequest) => {
          //   console.log(rawRequest.trim());
          let obj = JSON.stringify({
            functionName: "js_fun_start_pay",
            params: {
              rawRequest: rawRequest.trim(),
              functionCallBackName: "handleinitDataCallback",
            },
          });

          if (typeof rawRequest === undefined || rawRequest === null) return;
          if (window.consumerapp === undefined || window.consumerapp === null) {
            console.log("This is not opened in app!");
            return;
          }
          window.consumerapp.evaluate(obj);
        })
        .catch((error) => {
          console.log("error occur", error);
        })
        .finally(() => {});
    })
    .finally(() => {
      loading.hide();
    });
}
