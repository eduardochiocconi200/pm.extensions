import { useState } from "react";
import { Button, Form, InputGroup, Modal } from 'react-bootstrap';

function NewPhaseDialog({ showDialog, onValueChange })
{
    const [phaseName, setPhaseName] = useState('');

    const handleCloseNewPhaseDialog = () => {
        onValueChange(phaseName);
        console.log('Will close new Phase Dialog.');
    }

    const handleChange = (event) => {
        setPhaseName(event.target.value);
    }

    return (
        <>
            <Modal show={showDialog} onHide={handleCloseNewPhaseDialog}>
                <Modal.Header closeButton>
                    <Modal.Title>Provide the name of the new phase</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <InputGroup className="mb-3">
                        <Form.Control autoFocus onChange={handleChange} placeholder="Enter Phase Name" aria-label="Phase" aria-describedby="basic-addon1"/>
                    </InputGroup>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="primary" onClick={handleCloseNewPhaseDialog}>Add Phase</Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

export default NewPhaseDialog;