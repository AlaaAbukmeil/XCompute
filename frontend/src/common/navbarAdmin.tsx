import PaginationArrows from "./historyNavBar";

function NavbarAdmin(props: any) {
  let newTrades = parseInt(localStorage.getItem("newTrades") || "0");

  return (
    <div>
      <nav className="navbar navbar-expand-lg  navbar-custom">
        <div className="container-fluid">
          <a onClick={(event) => props.navigate(event, "/")}>
            <img src={"/photos/XCompute_logo.png"} className="header-logo" alt="logo-triada" />
          </a>
          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navbarSupportedContent">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0 header-items">
              <li className="nav-item dropdown">
                <a className="nav-link active header-item" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false" onClick={(event) => props.navigate(event, "/")}>
                  Prices
                </a>
                
              </li>
             
  
            </ul>
          </div>
        </div>
      </nav>
    </div>
  );
}

export default NavbarAdmin;
