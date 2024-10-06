import { useState } from "react";
import { Tooltip as ReactTooltip } from "react-tooltip";
export function getDate() {
  const date = new Date(); // Current date
  const formattedDate = date.toLocaleDateString("en-GB", {
    day: "2-digit", // two-digit day
    month: "2-digit", // two-digit month
    year: "numeric", // four-digit year
  });
  return formattedDate;
}

export function getDateFromInput(dateInput: string) {
  const date = new Date(dateInput); // Current date
  const formattedDate = date.toLocaleDateString("en-GB", {
    day: "2-digit", // two-digit day
    month: "2-digit", // two-digit month
    year: "numeric", // four-digit year
  });
  return formattedDate;
}

export function getCurrentDateFormatted(date: any) {
  const now = new Date(date); // Create a new date object for the current date and time

  const year = now.getFullYear(); // Get the full year (4 digits)

  // Get the month and add 1 (January is 0, December is 11), ensure it has two digits
  const month = String(now.getMonth() + 1).padStart(2, "0");

  // Get the day of the month, ensure it has two digits
  const day = String(now.getDate()).padStart(2, "0");

  // Return the formatted date string
  return `${year}-${month}-${day}`;
}

export function getLastMonthLastDay(date: any): any {
  const today = date;
  const lastDayOfLastMonth = new Date(today.getFullYear(), today.getMonth(), 0);

  let month = "" + (lastDayOfLastMonth.getMonth() + 1);
  let day = "" + lastDayOfLastMonth.getDate();
  let year = lastDayOfLastMonth.getFullYear();

  if (month.length < 2) month = "0" + month;
  if (day.length < 2) day = "0" + day;

  let lastMonthLastDay = [year, month, day].join("-");

  return lastMonthLastDay;
}

export function formatDate(date: any) {
  date = new Date(date); // Current date

  const formattedDate = date.toLocaleDateString("en-GB", {
    minute: "2-digit",
    hour: "2-digit",
    day: "2-digit", // two-digit day
    month: "2-digit", // two-digit month
    year: "numeric", // four-digit year
  });
  return formattedDate;
}
export function convertExcelDateToJSDate(serial: any) {
  const excelStartDate = new Date(1900, 0, 1);
  const correctSerial = serial - 2; //Excel and JS have different leap year behaviors
  const millisecondsInDay = 24 * 60 * 60 * 1000;

  const jsDate = new Date(excelStartDate.getTime() + correctSerial * millisecondsInDay);

  const year = jsDate.getFullYear();
  const month = jsDate.getMonth() + 1; // JavaScript months start at 0
  const day = jsDate.getDate();

  const formattedMonth = month < 10 ? "0" + month : month;
  const formattedDay = day < 10 ? "0" + day : day;

  return `${formattedDay}/${formattedDay}/${year}`;
}

export function getDateTimeInMongoDBCollectionFormat(date: any) {
  let today: any = new Date(date);

  let day = today.getDate();
  let month = today.getMonth() + 1;
  let year = today.getFullYear();
  let hours: any = today.getHours();
  let minutes: any = today.getMinutes();
  if (day < 10) {
    day = "0" + day;
  }
  if (month < 10) {
    month = "0" + month;
  }

  // Pad single digit minutes or hours with a leading zero
  if (hours < 10) hours = "0" + hours;
  if (minutes < 10) minutes = "0" + minutes;

  let formattedDateTime = year + "-" + month + "-" + day + " " + hours + ":" + minutes;
  return formattedDateTime;
}

export const RedStar = () => {
  return (
    <svg version="1.0" id="Layer_1" xmlns="http://www.w3.org/2000/svg" className="star-svg" xmlnsXlink="http://www.w3.org/1999/xlink" width="20px" height="20px" viewBox="0 0 64 64" enable-background="new 0 0 64 64" xmlSpace="preserve" fill="#000000">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round" stroke="#CCCCCC" stroke-width="2.816"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path
          fill="#ff1100"
          d="M62.799,23.737c-0.47-1.399-1.681-2.419-3.139-2.642l-16.969-2.593L35.069,2.265 C34.419,0.881,33.03,0,31.504,0c-1.527,0-2.915,0.881-3.565,2.265l-7.623,16.238L3.347,21.096c-1.458,0.223-2.669,1.242-3.138,2.642 c-0.469,1.4-0.115,2.942,0.916,4l12.392,12.707l-2.935,17.977c-0.242,1.488,0.389,2.984,1.62,3.854 c1.23,0.87,2.854,0.958,4.177,0.228l15.126-8.365l15.126,8.365c0.597,0.33,1.254,0.492,1.908,0.492c0.796,0,1.592-0.242,2.269-0.72 c1.231-0.869,1.861-2.365,1.619-3.854l-2.935-17.977l12.393-12.707C62.914,26.68,63.268,25.138,62.799,23.737z"
        ></path>{" "}
      </g>
    </svg>
  );
};
export const WhiteStar = () => {
  return (
    <svg xmlns="http://www.w3.org/2000/svg" fill="#ffffff" className="star-svg" viewBox="0 0 24 24" stroke="#ffffff">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        <path d="M23 13H2v-2h21v2z"></path>
      </g>
    </svg>
  );
};
export const GreenCheck = () => {
  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="star-svg eblot-svg">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path
          fill-rule="evenodd"
          clip-rule="evenodd"
          d="M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12ZM16.0303 8.96967C16.3232 9.26256 16.3232 9.73744 16.0303 10.0303L11.0303 15.0303C10.7374 15.3232 10.2626 15.3232 9.96967 15.0303L7.96967 13.0303C7.67678 12.7374 7.67678 12.2626 7.96967 11.9697C8.26256 11.6768 8.73744 11.6768 9.03033 11.9697L10.5 13.4393L12.7348 11.2045L14.9697 8.96967C15.2626 8.67678 15.7374 8.67678 16.0303 8.96967Z"
          fill="#19b841"
        ></path>{" "}
      </g>
    </svg>
  );
};
export const RedCross = () => {
  return (
    <svg viewBox="0 0 32 32" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" fill="#e70404" className="star-svg eblot-svg">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <title>cross-circle</title> <desc>Created with Sketch Beta.</desc> <defs> </defs>{" "}
        <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
          {" "}
          <g id="Icon-Set-Filled" transform="translate(-570.000000, -1089.000000)" fill="#e40707">
            {" "}
            <path
              d="M591.657,1109.24 C592.048,1109.63 592.048,1110.27 591.657,1110.66 C591.267,1111.05 590.633,1111.05 590.242,1110.66 L586.006,1106.42 L581.74,1110.69 C581.346,1111.08 580.708,1111.08 580.314,1110.69 C579.921,1110.29 579.921,1109.65 580.314,1109.26 L584.58,1104.99 L580.344,1100.76 C579.953,1100.37 579.953,1099.73 580.344,1099.34 C580.733,1098.95 581.367,1098.95 581.758,1099.34 L585.994,1103.58 L590.292,1099.28 C590.686,1098.89 591.323,1098.89 591.717,1099.28 C592.11,1099.68 592.11,1100.31 591.717,1100.71 L587.42,1105.01 L591.657,1109.24 L591.657,1109.24 Z M586,1089 C577.163,1089 570,1096.16 570,1105 C570,1113.84 577.163,1121 586,1121 C594.837,1121 602,1113.84 602,1105 C602,1096.16 594.837,1089 586,1089 L586,1089 Z"
              id="cross-circle"
            >
              {" "}
            </path>{" "}
          </g>{" "}
        </g>{" "}
      </g>
    </svg>
  );
};
export const CopyableCell = ({ text, index, columns, type, color, disabled, classNameParam, left }: any) => {
  const [copySuccess, setCopySuccess] = useState("");

  const copyToClipboard = async (text: any) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopySuccess("Copied!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    } catch (err) {
      // console.error("Failed to copy: ", err);
      setCopySuccess("Failed to copy!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    }
  };

  const handleCellClick = (event: any, disabled: any) => {
    if (!disabled) {
      copyToClipboard(text);
    }
  };

  return (
    <td
      onClick={(event) => handleCellClick(event, disabled)}
      style={{ cursor: "pointer", position: "relative", backgroundColor: type == "summary" || type == "portfolio" ? color : "", left: left ? left : "" }}
      className={"copied-text " + classNameParam + " " + (index < columns ? " sticky-" + (type == "summary" ? "summary" : "portfolio") : "")}
    >
      {isInteger(text) && parseFloat(text) < 0 ? text.replace("-","(") + ")" : text}
      
      {copySuccess && (
        <span
          style={{
            position: "absolute",
            right: "0",
            bottom: "0",
            backgroundColor: "black",
            color: "white",
            // fontSize: "0.75rem",
            padding: "2px 4px",
            borderRadius: "4px",
            pointerEvents: "none",
            opacity: "0.75",
            transition: "opacity 0.3s ease",
          }}
          className="copied-text"
        >
          {copySuccess}
        </span>
      )}
    </td>
  );
};
export const CopyableCellWithDifferentText = ({ text, value }: any) => {
  const [copySuccess, setCopySuccess] = useState("");

  const copyToClipboard = async (text: any) => {
    try {
      await navigator.clipboard.writeText(value);
      setCopySuccess("Copied!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    } catch (err) {
      // console.error("Failed to copy: ", err);
      setCopySuccess("Failed to copy!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    }
  };

  const handleCellClick = (event: any) => {
    copyToClipboard(value);
  };

  return (
    <td onClick={(event) => handleCellClick(event)} className="link">
      {isInteger(text) && parseFloat(text) < 0 ? text.replace("-","(") + ")" : text}

      {copySuccess && (
        <span
          style={{
            position: "absolute",
            backgroundColor: "black",
            color: "white",
            // fontSize: "0.75rem",
            padding: "2px 4px",
            borderRadius: "4px",
            pointerEvents: "none",
            opacity: "0.75",
            transition: "opacity 0.3s ease",
          }}
          className="copied-text"
        >
          {copySuccess}
        </span>
      )}
    </td>
  );
};
export function isInteger(value: any) {
  return !isNaN(parseInt(value, 10));
}
export const CopyableCellSimple = ({ text }: any) => {
  const [copySuccess, setCopySuccess] = useState("");

  const copyToClipboard = async (text: any) => {
    try {
      await navigator.clipboard.writeText(text);
      setCopySuccess("Copied!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    } catch (err) {
      console.error("Failed to copy: ", err);
      setCopySuccess("Failed to copy!");
      setTimeout(() => setCopySuccess(""), 2000); // Remove the message after 2 seconds
    }
  };

  const handleCellClick = (event: any) => {
    copyToClipboard(text);
  };

  return (
    <td onClick={(event) => handleCellClick(event)} className="clickable-nodecoration">
      {isInteger(text) && parseFloat(text) < 0 ? text.replace("-","(") + ")" : text}
      {copySuccess && (
        <span
          style={{
            position: "absolute",
            right: "20",
            bottom: "0",
            backgroundColor: "black",
            color: "white",
            // fontSize: "0.75rem",
            padding: "2px 4px",
            borderRadius: "4px",
            pointerEvents: "none",
            opacity: "0.75",
            transition: "opacity 0.3s ease",
          }}
          className="copied-text"
        >
          {copySuccess}
        </span>
      )}
    </td>
  );
};

export function Add() {
  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="add-svg">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path d="M15 12L12 12M12 12L9 12M12 12L12 9M12 12L12 15" stroke="#1C274C" stroke-width="1.5" stroke-linecap="round"></path>{" "}
        <path d="M7 3.33782C8.47087 2.48697 10.1786 2 12 2C17.5228 2 22 6.47715 22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 10.1786 2.48697 8.47087 3.33782 7" stroke="#1C274C" stroke-width="1.5" stroke-linecap="round"></path>{" "}
      </g>
    </svg>
  );
}
export function TriangleUp() {
  return (
    <svg fill="#000000" id="Layer_1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" className="triangle-svg">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path d="M8,2L2,14H14L8,2Z"></path>{" "}
      </g>
    </svg>
  );
}
export function TriangleDown() {
  return (
    <svg fill="#000000" id="Layer_1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" className="triangle-svg triangle-rotate">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path d="M8,2L2,14H14L8,2Z"></path>{" "}
      </g>
    </svg>
  );
}

export function getDurationName(duration: any) {
  console.log(duration, "test1");
  if (parseFloat(duration) === 0.0001) {
    duration = "Now";
  } else if (Math.round(duration * 100) / 100 === Math.round((45 / 365) * 100) / 100) {
    duration = "6 Weeks";
  } else if (Math.round(duration * 100) / 100 === Math.round((30 / 365) * 100) / 100) {
    duration = "1 Month";
  } else if (Math.round(duration * 100) / 100 === Math.round((90 / 365) * 100) / 100) {
    duration = "3 Months";
  } else if (Math.round(duration * 100) / 100 === Math.round((1 * 100) / 100)) {
    duration = "1 Year";
  } else if (Math.round(duration * 100) / 100 === Math.round((2 * 100) / 100)) {
    duration = "2 Years";
  } else if (Math.round(duration * 100) / 100 === Math.round((5 * 100) / 100)) {
    duration = "5 Years";
  } else if (Math.round(duration * 100) / 100 === Math.round((10 * 100) / 100)) {
    duration = "10 Years";
  } else if (Math.round(duration * 100) / 100 === Math.round((30 * 100) / 100)) {
    duration = "30 Years";
  } else {
    // If none of the conditions match, you could either throw an error, set a default, or leave it unchanged
    duration = "100 Years";
  }
  return duration;
}
export function Filter() {
  return (
    <svg viewBox="0 -0.5 21 21" version="1.1" className="filter-svg" xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" fill="#000000">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <title>filter [#1384]</title> <desc>Created with Sketch.</desc> <defs> </defs>{" "}
        <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd">
          {" "}
          <g id="Dribbble-Light-Preview" transform="translate(-299.000000, -760.000000)" fill="#000000">
            {" "}
            <g id="icons" transform="translate(56.000000, 160.000000)">
              {" "}
              <path d="M254.55945,610 L254.55,614 L252.45,614 L252.45,610 L246.63405,602 L260.367,602 L254.55945,610 Z M252.45,618 L254.55,618 L254.55,616 L252.45,616 L252.45,618 Z M243,600 L250.35,610.857 L250.35,620 L256.65,620 L256.65,610.857 L264,600 L243,600 Z" id="filter-[#1384]">
                {" "}
              </path>{" "}
            </g>{" "}
          </g>{" "}
        </g>{" "}
      </g>
    </svg>
  );
}
export function CloseIcon(props: any) {
  return (
    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className={"close-icon " + props.classNameInput}>
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <path
          d="M20.7457 3.32851C20.3552 2.93798 19.722 2.93798 19.3315 3.32851L12.0371 10.6229L4.74275 3.32851C4.35223 2.93798 3.71906 2.93798 3.32854 3.32851C2.93801 3.71903 2.93801 4.3522 3.32854 4.74272L10.6229 12.0371L3.32856 19.3314C2.93803 19.722 2.93803 20.3551 3.32856 20.7457C3.71908 21.1362 4.35225 21.1362 4.74277 20.7457L12.0371 13.4513L19.3315 20.7457C19.722 21.1362 20.3552 21.1362 20.7457 20.7457C21.1362 20.3551 21.1362 19.722 20.7457 19.3315L13.4513 12.0371L20.7457 4.74272C21.1362 4.3522 21.1362 3.71903 20.7457 3.32851Z"
          fill="#ffffff"
        ></path>{" "}
      </g>
    </svg>
  );
}

export function AdvancedTooltip(props: any) {
  return <ReactTooltip id={props.id} place="bottom" content={props.content} />;
}

export function Comment(props: any) {
  return (
    <svg className="comment-icon" version="1.1" data-tooltip-id={props.id} xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 512 512" xmlSpace="preserve" fill="white">
      <g id="SVGRepo_bgCarrier" stroke-width="0"></g>
      <g id="SVGRepo_tracerCarrier" stroke-linecap="round" stroke-linejoin="round"></g>
      <g id="SVGRepo_iconCarrier">
        {" "}
        <style type="text/css"> </style>{" "}
        <g>
          {" "}
          <path
            className="st0"
            d="M256,0C114.615,0,0,114.615,0,256s114.615,256,256,256s256-114.615,256-256S397.385,0,256,0z M256,86.069 c28.463,0,51.538,23.074,51.538,51.538c0,28.464-23.074,51.538-51.538,51.538c-28.463,0-51.538-23.074-51.538-51.538 C204.462,109.143,227.537,86.069,256,86.069z M310.491,425.931H201.51v-43.593h35.667V276.329H215.38v-43.593h65.389v3.963v39.63 v106.009h29.722V425.931z"
          ></path>{" "}
        </g>{" "}
      </g>
    </svg>
  );
}

export function returnMultipliedBy100AndAligned(input: any) {
  let minus = "- ";
  let positive = "+ ";
  return input || input == 0 ? (input >= 0 ? positive : minus) + Math.abs(Math.round(input * 10000) / 100).toFixed(2) : "";
}

export function returnAlignedMaxed(input: any) {
  let minus = "- ";
  let positive = "+ ";
  let max = "> ";
  return (input >= 0 ? (input >= 10 ? "> " : positive) : minus) + Math.abs(Math.round(input * 100) / 100).toFixed(2);
}
const removeLastChar = (str: string) => str.slice(0, -2);

export function getQueryParamters(query: string, override = "") {
  if (query == "") {
    return [];
  }
  let text: any = query.split("&");
  for (let index = 0; index < text.length; index++) {
    let textStart = text[index].replace("+", " ").split("=");
    if (textStart[0] && textStart[1]) {
      if (textStart[0] == "positions") {
        let temp = (camelCaseToNormalText(textStart[0]) + " = " + override).split("@").join(" & ");
        text[index] = temp;
      } else {
        let temp = (camelCaseToNormalText(textStart[0]) + " = " + textStart[1]).split("@").join(" & ");
        text[index] = temp;
      }
    } else {
      text.splice(index, 1);
      index--;
    }
  }
  if (text.length) {
    text[text.length - 1] = removeLastChar(text[text.length - 1].toString());
  }

  return text;
}
function camelCaseToNormalText(camelCaseString: any) {
  // Split the camelCase string into words based on uppercase letters
  const words = camelCaseString.match(/^[a-z]+|[A-Z][a-z]*|[^a-zA-Z]+/g);

  // Capitalize the first letter of each word and join them with spaces
  const normalText = words
    .map((word: any) => {
      // Only capitalize the first letter if the word is alphabetic
      if (/^[a-zA-Z]+$/.test(word)) {
        return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
      }
      return word;
    })
    .join(" ");

  return normalText;
}
export function formatDateTitle(date: any) {
  // Define suffixes for the days
  const suffixes: any = { 1: "st", 2: "nd", 3: "rd", 21: "st", 22: "nd", 23: "rd", 31: "st" };

  // Convert the date to a Date object if it's not already
  const d = new Date(date);

  // Get the day of the month
  const day = d.getDate();

  // Get the month name (first three letters)
  const monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
  const month = monthNames[d.getMonth()];

  // Determine the suffix for the day
  const suffix = suffixes[day] || "th";

  // Format the date string
  return `${month}' ${day}${suffix}`;
}
export default getDate;
