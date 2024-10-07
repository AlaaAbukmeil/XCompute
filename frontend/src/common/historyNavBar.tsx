import React from "react";
import { useNavigate } from "react-router-dom";

const PaginationArrows = ({ onPrevious, onNext }: any) => {
  let lastVisitedPages = [
    { pageName: "Summary", route: "/" },
    { pageName: "Exposure", route: "/view-exposure-summary" },
    { pageName: "Top/Worst Performers", route: "/view-performers" },
  ];
  const navigate = useNavigate();

  const handleClickBack = () => {
    navigate(-1);
  };
  const handleClickForward = () => {
    navigate(1);
  };
  return (
    <div className="history-nav">
      <div onClick={handleClickBack} className="nav-arrow-right">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
          <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
          <g id="SVGRepo_iconCarrier">
            {" "}
            <path d="M6 12H18M6 12L11 7M6 12L11 17" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>{" "}
          </g>
        </svg>
      </div>
      <div>
        {lastVisitedPages.map((page: any, index: any) => (
          <a key={index} href={page.route} className="last-visited-pages">
            {page.pageName}
          </a>
        ))}
      </div>
      <div onClick={handleClickForward} className="nav-arrow-left">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
          <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
          <g id="SVGRepo_iconCarrier">
            {" "}
            <path d="M6 12H18M18 12L13 7M18 12L13 17" stroke="#000000" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"></path>{" "}
          </g>
        </svg>
      </div>
    </div>
  );
};

export default PaginationArrows;
