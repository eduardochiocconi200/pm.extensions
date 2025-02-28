import { Button, Col, Container, Form, Modal, Row } from 'react-bootstrap';

function ActivityToPhaseMapping({ showPhaseMapping, handleShowPhaseMapping, phaseChanged, phases, activityToPhasesMapping })
{
    return (
        <>
            <Modal show={showPhaseMapping} onHide={handleShowPhaseMapping(true, false)} backdrop="static" keyboard={false}  size="lg" aria-labelledby="contained-modal-title-vcenter" centered>
            <Modal.Header closeButton>
                <Modal.Title>This is where you map each process map activity with a phase in the value stream.</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Container>
                    <Row>
                        <Col><b>Activity</b></Col>
                        <Col><b>Associated Phase</b></Col>
                    </Row>
                    <div style={{marginTop: '20px'}}></div>
                    <Row>
                        <Form>
                            {Array.from(activityToPhasesMapping.entries()).map(([key, value]) => (
                            <Form.Group key={key} as={Row} className="mb-3" controlId={key}>
                                <Form.Label column sm={2}>{key}:</Form.Label>
                                <Col sm={10}>
                                <Form.Select id={key} defaultValue={value} onChange={phaseChanged}>
                                    <option>Select one of the available value stream phase(s)</option>
                                    {phases.map((phase) => (
                                    <option value={phase.name}>{phase.name}</option>
                                    ))}
                                </Form.Select>
                                </Col>
                            </Form.Group>
                            ))}
                        </Form>
                    </Row>
                </Container>
            </Modal.Body>
            <Modal.Footer>
                <Button variant="secondary" onClick={ () => { handleShowPhaseMapping(false, false); } }>Close</Button>
                <Button variant="primary" onClick={ () => { handleShowPhaseMapping(false, true); } }>Update Phase Mapping(s)</Button>
            </Modal.Footer>
            </Modal>
        </>
    );
}

export default ActivityToPhaseMapping;