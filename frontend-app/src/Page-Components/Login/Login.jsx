/* eslint-disable no-unused-vars */
import { useEffect, useState } from 'react';
import { EncryptionDecryption } from '../../Common/EncryptionDecryption';
import { get_ip_address } from '../../Common/Utils';
import { AuthenticationService } from '../../Service/AuthenticationService';
import google from '../../../public/Images/google.svg';
import './Login.css';
import AlertModal from '../Common-Components/AlertModal/AlertModal';


function Login() {
  const [showModal, setShowModal] = useState(false);

  const encryptionDecryption = new EncryptionDecryption();
  const authenticationService = new AuthenticationService();

  useEffect(() => {
    const signUpButton = document.getElementById('signUp');
    const signInButton = document.getElementById('signIn');
    const container = document.getElementById('container');

    signUpButton.addEventListener('click', () => {
      container.classList.add("right-panel-active");
    });

    signInButton.addEventListener('click', () => {
      container.classList.remove("right-panel-active");
    });
  },[]);


  async function DoSignUp(){
    let obj = {
      first_name : document.getElementById("signup_firstname").value,
      last_name : document.getElementById("signup_lastname").value,
      email : document.getElementById("signup_email").value,
      password : encryptionDecryption.customEncrypt(document.getElementById("signup_password").value),
    }; 

    let response = await authenticationService.DoSignUpService(obj);
    console.log(response);
  }

  function openAlertModal(){
    setShowModal(true);
  }

  function closeAlertModal(){
    setShowModal(false);
  }


  return (
    <>
    <button type="button" className="btn btn-info btn-lg" onClick={openAlertModal}>
          Open Modal
        </button>
        <AlertModal showModal={showModal} handleClose={closeAlertModal} />
      <div className='login-container'>  
        <div className="container" id="container">
          <div className="form-container sign-up-container">

            <div className='form-div'>
              <h1>Create Account</h1>

              <div className="social-container">
                <a href="#" className="social"><img src={google}></img></a>
              </div> 
              <span>or use your email for registration</span>

              <input type="text" placeholder="First Name" id="signup_firstname"/>
              <input type="text" placeholder="Last Name" id="signup_lastname"/>
              <input type="email" placeholder="Email" id="signup_email"/>
              <input type="password" placeholder="Password" id="signup_password"/>

              <button onClick={DoSignUp}>Sign Up</button>
            </div>
          </div>

          <div className="form-container sign-in-container">
            <div className='form-div'>
              <h1>Sign in</h1>

              <div className="social-container">
                <a href="#" className="social"><img src={google}></img></a>
              </div> 
              <span>or use your account</span>

              <input type="email" placeholder="Email" id="login_email"/>
              <input type="password" placeholder="Password" id="login_password"/>

              <a href="#">Forgot your password?</a>
              <button>Sign In</button>
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
