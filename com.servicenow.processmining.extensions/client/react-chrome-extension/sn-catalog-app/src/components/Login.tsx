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
        setUserError("Please enter your user")
        return
    }

    if ("" === password) {
        setPasswordError("Please enter a password")
        return
    }

    if (password.length < 7) {
      setPasswordError("The password must be 8 characters or longer");
      return;
    }

    if ("" === instance) {
      setInstanceError("Please enter the name of a ServiceNow Instance password");
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
          setLoginError('Could not be logged into the ServiceNow instance: ' + instance);
        }
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err);
        setLoginError('Could not be logged into the ServiceNow instance: ' + instance);
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
                className={"inputButton"}
                type="button"
                onClick={onButtonClick}
                value={"Log in"} />
            <label className="errorLabel">{loginError}</label>                
        </div>
    </div>)
}

export default Login;