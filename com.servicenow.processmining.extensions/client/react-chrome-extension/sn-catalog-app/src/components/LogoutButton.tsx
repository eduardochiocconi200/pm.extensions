import { useNavigate } from "react-router-dom";
import axios from "axios";

function LogoutButton() {

    const navigate = useNavigate();

    const onButtonClick = () => {
      logOut();
    }
  
    function logOut() {
      const url = 'http://localhost:8080/logout';
      axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*"} })
        .then(response => response.data)
        .then((data) => {
          console.log(data);
          if (data == "success") {
            console.log('Successfully logged out from the ServiceNow instance');
            navigate("/")
          }
          else if (data == "error") {
            console.log('Could not log out from the ServiceNow instance');
          }
        })
        .catch((err) => {
          console.log("AXIOS ERROR: ", err);
          console.log('Could not log out from the ServiceNow instance');
        })
    }  
    
    return (
        <>
          <div className={"inputContainer"}>
              <input
                  className={"inputButton"}
                  type="button"
                  onClick={onButtonClick}
                  value={"Log out"} />
          </div>
        </>
    );
}

export default LogoutButton;