/* eslint-disable no-unused-vars */
import { useEffect, useState } from 'react';
import { EncryptionDecryption } from '../../Common/EncryptionDecryption';
import { Environment } from '../../Environment/Environment';
import { get_ip_address, redirect_to_home } from '../../Common/Utils';
import { AuthenticationService } from '../../Service/AuthenticationService';
import google from '../../../public/Images/google.svg';
import './Login.css';
import AlertModal from '../Common-Components/AlertModal/AlertModal';
import { useNavigate } from 'react-router-dom';

let loadAlertModal = null;
let api_response_status = null;

function Login() {
  const navigate = useNavigate();

  const JWT = localStorage.getItem("JWT");

  const [showAlertModal, setShowAlertModal] = useState(false);
  const [headerTextOfAlertModal, setHeaderTextOfAlertModal] = useState(null);
  const [bodyTextOfAlertModal, setBodyTextOfAlertModal] = useState(null);
  const [colorOfAlertModal, setColorOfAlertModal] = useState('green');


  const encryptionDecryption = new EncryptionDecryption();
  const authenticationService = new AuthenticationService();


  useEffect(() => {
    if (JWT !== undefined && JWT !== null) {
      redirect_to_home(navigate);
    }

    const signUpButton = document.getElementById('signUp');
    const signInButton = document.getElementById('signIn');
    const container = document.getElementById('container');

    signUpButton.addEventListener('click', () => {
      container.classList.add("right-panel-active");
    });

    signInButton.addEventListener('click', () => {
      container.classList.remove("right-panel-active");
    });
  }, []);


  function ValidateSignUpFormData(first_name, last_name, email, password, confirm_password) {
    let validationStatus = true;
    let warning_message = "";

    if (first_name === "" || first_name === null) {
      warning_message = "Firstname can't be empty.";
      validationStatus = false;
    } else if (last_name === "" || last_name === null) {
      warning_message = "Lastname can't be empty.";
      validationStatus = false;
    } else if (email === "" || email === null) {
      warning_message = "Email can't be empty.";
      validationStatus = false;
    } else if (password === "" || password === null) {
      warning_message = "Password can't be empty.";
      validationStatus = false;
    } else if (confirm_password === "" || confirm_password === null) {
      warning_message = "Confirm Password can't be empty.";
      validationStatus = false;
    } else if (password !== confirm_password) {
      warning_message = "Password and Confirm password is not matching.";
      validationStatus = false;
    }

    if (validationStatus === false) {
      setColorOfAlertModal(Environment.colorWarning);
      openAlertModal(Environment.alert_modal_header_signup, warning_message);
    }

    return validationStatus;
  }


  async function DoSignUp() {
    api_response_status = null;
    closeAlertModal();

    let first_name = document.getElementById("signup_firstname").value;
    let last_name = document.getElementById("signup_lastname").value;
    let email = document.getElementById("signup_email").value;
    let password = document.getElementById("signup_password").value;
    let confirm_password = document.getElementById("signup_confirm_password").value;

    if (ValidateSignUpFormData(first_name, last_name, email, password, confirm_password) === false) return;

    let obj = {
      first_name: first_name,
      last_name: last_name,
      email: email,
      password: encryptionDecryption.Encrypt(password),
    };

    let response = await authenticationService.DoSignUpService(obj);
    api_response_status = response.status;

    if (api_response_status === 200) {
      setColorOfAlertModal(Environment.colorSuccess);

      document.getElementById("signup_firstname").value = null;
      document.getElementById("signup_lastname").value = null;
      document.getElementById("signup_email").value = null;
      document.getElementById("signup_password").value = null;
      document.getElementById("signup_confirm_password").value = null;
    } else {
      setColorOfAlertModal(Environment.colorError);
    }

    openAlertModal(Environment.alert_modal_header_signup, response.message);

    loadAlertModal = setTimeout(() => {
      closeAlertModal();
    }, 5000);
  }


  function ValidateLoginFormData(email, password) {
    let validationStatus = true;
    let warning_message = "";

    if (email === "" || email === null) {
      warning_message = "Email can't be empty.";
      validationStatus = false;
    } else if (password === "" || password === null) {
      warning_message = "Password can't be empty.";
      validationStatus = false;
    }

    if (validationStatus === false) {
      setColorOfAlertModal(Environment.colorWarning);
      openAlertModal(Environment.alert_modal_header_login, warning_message);
    }

    return validationStatus;
  }


  async function DoLogin() {
    api_response_status = null;
    closeAlertModal();

    let email = document.getElementById("login_email").value;
    let password = document.getElementById("login_password").value;

    if (ValidateLoginFormData(email, password) === false) return;

    let obj = {
      email: email,
      password: encryptionDecryption.Encrypt(password),
      ip_address: await get_ip_address()
    };

    let response = await authenticationService.DoLoginService(obj);
    api_response_status = response.status;

    if (api_response_status !== 200) {
      setColorOfAlertModal(Environment.colorError);

      openAlertModal(Environment.alert_modal_header_login, response.message);
    } else {
      localStorage.removeItem("JWT");
      localStorage.setItem("JWT", JSON.stringify(response.data));
      redirect_to_home(navigate);
    }

    loadAlertModal = setTimeout(() => {
      closeAlertModal();
    }, 5000);
  }


  function openAlertModal(header_text, body_text) {
    setHeaderTextOfAlertModal(header_text);
    setBodyTextOfAlertModal(body_text);
    setShowAlertModal(true);
  }


  function closeAlertModal() {
    setShowAlertModal(false);
    setHeaderTextOfAlertModal(null);
    setBodyTextOfAlertModal(null);

    clearTimeout(loadAlertModal);
    loadAlertModal = null;

    if (api_response_status === 200) {
      if(document.getElementById('signIn')){
        document.getElementById('signIn').click();
      }
      api_response_status = null;
    }
  }


  return (
    <>
      <div className='login-container'>
        <AlertModal showModal={showAlertModal} handleClose={closeAlertModal} headerText={headerTextOfAlertModal} bodyText={bodyTextOfAlertModal} alertColor={colorOfAlertModal} />
        <div className="container" id="container">
          <div className="form-container sign-up-container">

            <div className='form-div'>
              <h1>Create Account</h1>

              <div className="social-container">
                <a href="#" className="social"><img src={google}></img></a>
              </div>
              <span>or use your email for registration</span>

              <input type="text" placeholder="First Name" id="signup_firstname" />
              <input type="text" placeholder="Last Name" id="signup_lastname" />
              <input type="email" placeholder="Email" id="signup_email" />
              <input type="password" placeholder="Password" id="signup_password" />
              <input type="password" placeholder="Confirm Password" id="signup_confirm_password" />

              <button className="signup_btn" onClick={DoSignUp}>Sign Up</button>
            </div>
          </div>

          <div className="form-container sign-in-container">
            <div className='form-div'>
              <h1>Sign in</h1>

              <div className="social-container">
                <a href="#" className="social"><img src={google}></img></a>
              </div>
              <span>or use your account</span>

              <input type="email" placeholder="Email" id="login_email" />
              <input type="password" placeholder="Password" id="login_password" />

              <a href="#">Forgot your password?</a>
              <button onClick={DoLogin}>Sign In</button>
            </div>
          </div>

          <div className="overlay-container">
            <div className="overlay">
              <div className="overlay-panel overlay-left">
                <h1>Welcome Back!</h1>
                <p>To keep connected with us please login with your personal info</p>
                <button className="ghost" id="signIn">Sign In</button>
              </div>
              <div className="overlay-panel overlay-right">
                <h1>Hello, Friend!</h1>
                <p>Enter your personal details and start journey with us</p>
                <button className="ghost" id="signUp">Sign Up</button>
              </div>
            </div>

          </div>
        </div>
      </div>
    </>
  )
}

export default Login;