import PropTypes from "prop-types"; 

function AlertModal({ showModal, handleClose }) {
  return (
    <>
      {showModal && (
        <div className="modal fade in" style={{ display: "block", backgroundColor: "rgba(0, 0, 0, 0.5)" }} tabIndex="-1">
          <div className="modal-dialog">
            <div className="modal-content">
              <div className="modal-header">
                <button type="button" className="close" onClick={handleClose}>&times;</button>
                <h4 className="modal-title">Modal Header</h4>
              </div>

              <div className="modal-body">
                <p>Some text in the modal.</p>
              </div>

              <div className="modal-footer">
                <button type="button" className="btn btn-default" onClick={handleClose}>Close</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

AlertModal.propTypes = {
  showModal: PropTypes.bool.isRequired, 
  handleClose: PropTypes.func.isRequired,
};

export default AlertModal;
