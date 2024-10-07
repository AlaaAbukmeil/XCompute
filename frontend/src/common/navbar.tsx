import { useEffect, useState } from "react";
import { baseUrl, getRequestOptions, handleAuth, postRequestOptions } from "./cookie";
import NavbarAdmin from "./navbarAdmin";
import axios from "axios";
import { useLocation } from "react-router-dom";
export function navigate(event: any, page: string) {
  window.location.href = page;
}
function NavBar(props: any) {
  async function handleLogOut(event: any) {
    try {
      let auth = await axios.post(baseUrl + "logout", {}, postRequestOptions);
    } catch (error) {}
    localStorage.setItem("accessRole", "");
    localStorage.setItem("shareClass", "");

    window.location.href = "/login";
  }
  let query = new URLSearchParams(useLocation().search);
  let [token, setToken] = useState(query.get("token"));
  let [investorReports, setInvestorReports] = useState({});

  let [emailToken, setEmailToken] = useState("");

  useEffect(() => {
    if (token) {
      const email = window.prompt("Please enter your email address:");
      if (email) {
        setEmailToken(email);
      }
    }
  }, [token]);

  let url = `${baseUrl}auth?token=${token}&email=${emailToken}`;
  let accessRole = localStorage.getItem("accessRole");

  useEffect(() => {
    fetch(url, getRequestOptions)
      .then((res) => {
        handleAuth(res.status);
        return res.json();
      })
      .then((data) => {
        localStorage.setItem("accessRole", data.accessRole);
        localStorage.setItem("shareClass", data.shareClass);
        localStorage.setItem("newTrades", data.newTrades);
        setInvestorReports(data.investorReports);
      })
      .catch((error) => {
        console.error("Fetch error:", error);
      });
  }, [url]);
  if (props.redirect && emailToken) {
    console.log(accessRole);
    setTimeout(() => {
      window.location.href = props.redirect;
    }, 2000);
  }

  return <NavbarAdmin navigate={navigate} handleLogOut={handleLogOut} navigation={props.navigation != undefined ? props.navigation : true} investorReports={investorReports} />;
}
export default NavBar;
