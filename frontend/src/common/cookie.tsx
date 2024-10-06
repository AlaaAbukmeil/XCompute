export const getRequestOptions: any = {
  method: "GET",
  credentials: "include",
};
export const postRequestOptionsFetch: any = {
  method: "POST",
  credentials: "include",
};
//api.triadacapital.com
export let baseUrl = "http://localhost:8081/api/";

export const postRequestOptions: any = {
  method: "POST",
  withCredentials: "include",
};
export const getAxiosRequestOptions: any = {
  method: "GET",
  withCredentials: "include",
};

export const handleAuth = (status: number) => {
  if (status == 200) {
  } else if (status == 401) {
    window.location.href = "/login";
  }
};

export default {};
