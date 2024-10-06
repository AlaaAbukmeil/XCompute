import { useRouteError } from "react-router-dom";
import NavBar from "./navbar";
export default function Unavaliable() {
  const error: any = useRouteError();
  console.error(error);

  return (
    <div id="error-page">
      <NavBar />

      <div className="main-error-page">
        <div className="error-svg-container">
          <svg fill="#000000" height="200px" width="150px" className="rotating-svg" version="1.1" id="XMLID_218_" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 24 24" enable-background="new 0 0 24 24" xmlSpace="preserve">
            <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
            <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
            <g id="SVGRepo_iconCarrier">
              {" "}
              <g id="update">
                {" "}
                <g>
                  {" "}
                  <path d="M12,24c-4.1,0-7.8-2-10-5.4V24H0v-9h9v2H3.3c1.8,3.1,5,5,8.7,5c5.5,0,10-4.5,10-10h2C24,18.6,18.6,24,12,24z M2,12H0 C0,5.4,5.4,0,12,0c4.1,0,7.8,2,10,5.4V0h2v9h-9V7h5.7c-1.8-3.1-5-5-8.7-5C6.5,2,2,6.5,2,12z"></path>{" "}
                </g>{" "}
              </g>{" "}
            </g>
          </svg>
        </div>
        <div className="error-container">
          <h1 className="error-svg-text">Notice of Maintenance and Enhancements</h1>
          <h4 className="error-svg-text error-svg-text-des">
            We are currently updating the app to enhance your document access experience, including the Investment Factsheet. During these updates, the app may occasionally be unavailable. We will notify you via email once the latest monthly updates are available.
          </h4>
        </div>
      </div>
    </div>
  );
}
