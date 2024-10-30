import { baseUrl, postRequestOptions } from "../../common/cookie";
import Loader from "../../common/loader";
import { useState } from "react";
import axios from "axios";
import { useLocation } from "react-router-dom";
function Login() {
  let [requestStatus, setRequestStatus] = useState(false);
  let [authStatus, setAuthStatus] = useState("");
  let query = new URLSearchParams(useLocation().search);

  let [email, setEmail] = useState(localStorage.getItem("email") || query.get("email") || "");
  let [password, setPassword] = useState("");
  async function handleSubmit(event: any) {
    event.preventDefault();
    setRequestStatus(true);
    let formData = { email: email, password: password };
    
    try {
      let auth = await axios.post(baseUrl + "login", formData, postRequestOptions);
      console.log({auth})
      if (auth.status == 200) {
        window.location.href = "/";
      } else {
        setAuthStatus(auth.data.message);
        setRequestStatus(false);
      }
    } catch (error) {}
  }

  function OnChangeUsername(event: any) {
    setEmail(event.target.value);
  }
  function onChangePassword(event: any) {
    setPassword(event.target.value);
  }

  if (requestStatus) {
    return (
      <div>
        <Loader />
      </div>
    );
  }

  return (
    <div>
      <div className="container mt-5">
        <h1 style={{color:"green"}}>XCompute</h1>
      </div>

      <div className="row">
        <form className=" login-card container mt-5" id="loginForm">
          <div>
            <h4>
              <b>Email</b>
            </h4>
            <input type="email" className="formTextInput" title="email" name="email" value={email} onChange={(event) => OnChangeUsername(event)} />
          </div>
          <div>
            <h4>
              <b>Password</b>
            </h4>
            <input type="password" className="formTextInput" title="password" name="password" value={password} onChange={(event) => onChangePassword(event)} />
          </div>

          {authStatus ? <h4 className="error">{authStatus}</h4> : ""}
          <div className="auth-btn-container">
            <button type="submit" onClick={(event) => handleSubmit(event)} className="btn upload-btn auth-btn-1">
              Login
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
export default Login;
