import PropTypes from "prop-types";
import './AlertModal.css';

function AlertModal({ showModal, handleClose, headerText, bodyText, alertColor }) {
  return (
    <>
      <div className={`modal ${showModal ? 'show' : ''}`} tabIndex="-1">
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-header">
              <button type="button" className="close-alert-modal" onClick={handleClose}>&times;</button>
              <h4 className="modal-title">{headerText}</h4>
            </div>

            <div className="modal-body">
              <p style={{ color: alertColor }}>{bodyText}</p>
            </div>

            <div className="modal-footer">
              <button type="button" className="btn-alert-modal btn-alert-modal-default" style={{ backgroundColor: alertColor }} onClick={handleClose}>Close</button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

AlertModal.propTypes = {
  showModal: PropTypes.bool.isRequired,
  handleClose: PropTypes.func.isRequired,
  headerText: PropTypes.string,
  bodyText: PropTypes.string,
  alertColor: PropTypes.string.isRequired
};

export default AlertModal;
