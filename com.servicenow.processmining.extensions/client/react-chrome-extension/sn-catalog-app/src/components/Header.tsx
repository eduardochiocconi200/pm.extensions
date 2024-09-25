import LogoutButton from "./LogoutButton";
import HeaderWelcome from "./HeaderWelcome";

function Header() {    
    return (
        <>
          <div className='rowC'>
            <HeaderWelcome />
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <LogoutButton />
          </div>
          <p>This application works and leverages created Process Mining projects, and its primary goal is to help assess productivity gains you can find by leveraging a Process Mining centric discovery and analysis effort.
          In order to work on a given business process productivity analysis, you will need to select a particular process mining snapshot from the drop down list below and then the different project filters for the specific productivity breakdown assets.</p>
        </>
    );
}

export default Header;