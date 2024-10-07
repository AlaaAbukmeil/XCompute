import { createBrowserRouter } from "react-router-dom";
import SignUp from "../components/auth/signUp";
import Login from "../components/auth/login";
import ErrorPage from "../common/errorPage";
import Dashboard from "../components/dashboard/prices";
const router = createBrowserRouter([
 
  {
    path: "/sign-up",
    element: <SignUp />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/login",
    element: <Login />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/",
    element: <Dashboard />,
    errorElement: <ErrorPage />,
  },
]);

export default router;
