import Header from './Header';
import PMProjectBody from './PMBody';
import Footer from './Footer';
import Alert from './Alert';
import { useState } from "react";

function Home() {
  const [alertVisible, setAlertVisibility] = useState(false);

  return (
    <div className="container">
      <Header></Header>
      { alertVisible && <Alert onClose={ () => setAlertVisibility(false) } color="alert-success"><b>Information loaded.</b></Alert> }
      <PMProjectBody></PMProjectBody>
      <Footer></Footer>
    </div>)
}

export default Home;