<template>
  <div class="login-container">
    <div class="login-box">
      <h2>简单记账</h2>
      <form @submit.prevent="login">
        <div class="input-group">
          <label for="username">用户名</label>
          <input type="text" id="username" v-model="username" placeholder="输入用户名" required />
        </div>
        <div class="input-group">
          <label for="password">密码</label>
          <input type="password" id="password" v-model="password" placeholder="输入密码" required />
        </div>
        <button type="submit" class="btn">登录</button>
      </form>
      <!--
      <p class="signup-link">Don't have an account? <a href="#">Sign up</a></p>
      -->
    </div>
  </div>
</template>

<script>
import request from "../../utils/request";
import { Toast} from 'vant';

export default {
  data() {
    return {
      username: '',
      password: '',
      token: null,
    };
  },
  methods: {
    login() {
      if (this.username == null || this.username == "") {
        Toast.fail('请输入用户名');
        return
      }
      if (this.password == null || this.password == "") {
        Toast.fail('请输入用户密码');
        return
      }
      request({
        url: "/auth/login",
        method: "post",
        data: {
          username: this.username,
          password: this.password
        }
      }).then((response) => {
        this.token = response.data.data;
        localStorage.setItem('token', this.token);  // 保存 token
        Toast('登录成功');
        this.$router.push('/flow');  // Redirect to home
      }).catch((error) => {
        Toast.fail('登录失败！');
        console.error(error);
        Toast.error('Invalid credentials');
      });
    }
  }
};
</script>

<style scoped>
/* Reset some basic styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  height: 100%;           /* Ensure body and html take full height */
  font-family: 'Arial', sans-serif;
  background-color: #f7f7f7;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  
}

.login-box {
  background-color: white;
  padding: 40px 30px;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  height: 100%;
  text-align: center;
}

h2 {
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}

.input-group {
  margin-bottom: 20px;
  text-align: left;
}

.input-group label {
  display: block;
  margin-bottom: 5px;
  font-size: 14px;
  color: #666;
}

.input-group input {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  outline: none;
}

.input-group input:focus {
  border-color: #2575fc;
}

button {
  width: 100%;
  padding: 14px;
  background-color: #2575fc;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #6a11cb;
}

.signup-link {
  margin-top: 15px;
  font-size: 14px;
}

.signup-link a {
  color: #2575fc;
  text-decoration: none;
}

.signup-link a:hover {
  text-decoration: underline;
}

/* Mobile responsiveness */
@media (max-width: 480px) {
  .login-box {
    padding: 30px 20px;
  }

  h2 {
    font-size: 20px;
  }

  .input-group input {
    font-size: 14px;
  }

  button {
    padding: 12px;
    font-size: 14px;
  }
}
</style>