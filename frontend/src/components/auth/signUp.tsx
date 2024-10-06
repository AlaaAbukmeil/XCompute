import { baseUrl, postRequestOptions } from "../../common/cookie";
import Loader from "../../common/loader";
import { useState } from "react";
import axios from "axios";

function SignUp() {
  let [requestStatus, setRequestStatus] = useState(false);
  let [authStatus, setAuthStatus] = useState("");
  let [email, setEmail] = useState("");
  let [password, setPassword] = useState("");
  let [verficationCode, setVerficationCode] = useState("");
  async function handleSubmit(event: any) {
    event.preventDefault();
    setRequestStatus(true);
    let formData = { email: email, password: password, verificationCode: verficationCode };
    try {
      let auth: any = await axios.post(baseUrl + "sign-up", formData, postRequestOptions);
      console.log(auth);
      if (auth.data.status == 200) {
        localStorage.setItem("token", auth.data.token);
        window.location.href = "/login";
      } else {
        console.log(auth.data.message);
        setAuthStatus(auth.data.message);
      }
    } catch (error) {
      console.log(error);
    }

    setRequestStatus(false);
  }
  function OnChangeEmail(event: any) {
    setEmail(event.target.value);
  }
  function onChangePassword(event: any) {
    setPassword(event.target.value);
  }
  function onChangeVerficationCode(event: any) {
    setVerficationCode(event.target.value);
  }
  function handleLogin(event: any) {
    window.location.href = "/login";
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
        <h1>Triada Capital Admin Sign Up</h1>
        <p>Only for internal use, if you are a client, please contact jm@triadacapital.com</p>
      </div>

      <div className="row">
        <form className="col-10 login-card container mt-5" id="loginForm">
          <div>
            <h4>
              <b>Email</b>
            </h4>
            <input type="email" className="formTextInput" title="email" name="email" value={email} onChange={(event) => OnChangeEmail(event)} />
          </div>
          <div>
            <h4>
              <b>Password</b>
            </h4>
            <input type="password" className="formTextInput" title="password" name="password" value={password} onChange={(event) => onChangePassword(event)} />
          </div>
          <div>
            <h4>
              <b>Auth Code</b>
            </h4>
            <input type="password" className="formTextInput" placeholder="Contact Triada Admin to get auth code" title="verificationCode" name="verificationCode" value={verficationCode} onChange={(event) => onChangeVerficationCode(event)} />
          </div>
          {authStatus && <h4 className="error">{authStatus}</h4>}
          <p className="btn" onClick={(event) => handleLogin(event)}>
            Already have an account? Login
          </p>
          <div className="auth-btn-container">
            <button type="submit" onClick={(event) => handleSubmit(event)} className="btn upload-btn auth-btn-1">
              Sign Up
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
export default SignUp;
