import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Login() {
  const [user, setUser] = useState("")
  const [password, setPassword] = useState("")
  const [instance, setInstance] = useState("")
  const [userError, setUserError] = useState("")
  const [passwordError, setPasswordError] = useState("")
  const [instanceError, setInstanceError] = useState("")
  const [loginError, setLoginError] = useState("")

  const navigate = useNavigate();

  const onButtonClick = () => {
    // Set initial error values to empty
    setUserError("")
    setPasswordError("")
    setInstanceError("")
    setLoginError("")

    // Check if the user has entered both fields correctly
    if ("" === user) {
        setUserError("Please provide a user in the target SN Instance to connect to.")
        return
    }

    if ("" === password) {
        setPasswordError("Provide the user's password in the target SN Instance to connect to.")
        return
    }

    if ("" === instance) {
      setInstanceError("Provide the name of a ServiceNow Instance you want to connect to.");
      return;
    }

    logIn();
  }

  function logIn() {
    const url = 'http://localhost:8080/login/' + instance + '/' + user + '/' + password;
    axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log(data);
        if (data == "success") {
          console.log('Successfully logged into the ServiceNow instance: ' + instance);
          navigate("/home")
        }
        else if (data == "error") {
          console.log('Could not log into the ServiceNow instance: ' + instance);
          setLoginError('Error Details: Could not be logged into the ServiceNow instance: ' + instance);
        }
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err);
        setLoginError('Error Details: Could not connect to the Process Optimization Center service. The Service may be down. Check with the administrator.');
      })
  }  

  return (
      <div className={"mainContainer"}>
        <h2>Welcome to the ServiceNow Process Optimization Center</h2>
        <h3>Please Sign In to a ServiceNow Instance</h3>

        <br />
        <div className={"inputContainer"}>
            <input
                value={user}
                placeholder="Enter your email here"
                onChange={ev => setUser(ev.target.value)}
                className={"inputBox"} />
            <label className="errorLabel">{userError}</label>
        </div>
        <br />
        <div className={"inputContainer"}>
            <input
                value={password}
                placeholder="Enter your password here"
                onChange={ev => setPassword(ev.target.value)}
                className={"inputBox"} />
            <label className="errorLabel">{passwordError}</label>
        </div>
        <br />
        <div className={"inputContainer"}>
            <input
                value={instance}
                placeholder="Enter your ServiceNow Instance name here"
                onChange={ev => setInstance(ev.target.value)}
                className={"inputBox"} />
            <label className="errorLabel">{instanceError}</label>
        </div>
        <br />
        <div className={"inputContainer"}>
            <input
                value={"Log in"}
                type="button"
                onClick={onButtonClick}
                className={"inputButton"} />
            <br></br>
          </div>
          <div className={"inputContainer"}>
            <label className="errorLabel">{loginError}</label>                
        </div>
    </div>)
}

export default Login;