import axios from "axios";
import router from "@/router";
import { Notify } from "vant";

// const token = localStorage.getItem("token");

// // 设置请求头
// axios.defaults.headers.common["Authorization"] = `Bearer2 ${token}`;

function request(options) {
  return axios(options)
    .then((response) => {
      if (response.data.code != 0) {
        Notify({
          type: "warning",
          message: response.data.code + "\n" + response.data.msg,
          duration: 800,
        });
        caches();
      }
      return response;
    })
    .catch((err) => {
      const {
        response: { status, statusText },
      } = err;
      Notify({
        type: "warning",
        message: status + statusText + "\n" + options.url,
        duration: 800,
      });
      if(status == '403'){
        localStorage.removeItem("token");
        router.push('/login');  // Redirect to home
      }
      return Promise.reject(err);
    });
}

export default request;
