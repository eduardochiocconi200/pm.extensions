import { useEffect, useState } from "react";
import { Button, CardGroup, Card, Col, Container, Form, InputGroup, Modal, Row } from 'react-bootstrap';
import NewPhaseDialog from "./NewPhaseDialog";
import axios from "axios";

interface VStreamType {
  valueStream : {
    model: string,
    nodes: string[],
    phases: ValueStreamPhaseType[]
  },
  pk : {
    sys: string
  }
}

interface ValueStreamPhaseType {
  name: string,
  nodes: string[],
  statistics: {
    summary: ValueStreamPhaseMeasureType,
    measures: ValueStreamPhaseMeasureType[]
  }
}

interface ValueStreamPhaseMeasureType {
  variant: string,
  frequency: number,
  touchpoints: number,
  averageTime: number,
  meanTime: number,
  minTime: number,
  maxTime: number
}

function ValueStream(props: { model: string; }) {
  const [selectedPhase, setSelectedPhase] = useState<string>('');
  const [vStream, setVStream] = useState<VStreamType>();
  const [phases, setPhases] = useState<ValueStreamPhaseType[]>([]);
  const [showPhaseMapping, setShowPhaseMapping] = useState(false);
  const [showNewPhaseDialog, setShowNewPhaseDialog] = useState(false);
  const [activityToPhasesMapping, setActivityToPhasesMapping] = useState(new Map());

  const phaseChanged = (event) => {
    const activity = event.target.id;
    const phase = event.target.value;
    const newMap = activityToPhasesMapping;
    newMap.delete(activity);
    newMap.set(activity, phase);
    setActivityToPhasesMapping(newMap);
  }

  // We ignore any settings and do not sync with the Value Phase object.
  const handleClosePhaseMapping = () => {
    setShowPhaseMapping(false);
    console.log('Will disregard all mappings and will not synchronize with Value Stream.');
  }

  // We traverse the set mappings and synchronize with the Value Phase object.
  const handleSavePhaseMapping = () => {
    setShowPhaseMapping(false);
    console.log('Will synchronize with Value Stream.');
    for (const p of phases) {
      p.nodes = [];
    }
    for (const [key, value] of activityToPhasesMapping.entries()) {
      const ph = phases.find(p => p.name === value);
      ph!.nodes.push(key);
    }
  }

  const handleShowPhaseMapping = () => {
    setShowPhaseMapping(true);
  }

  const handleNewPhaseName = (value : string) => {
    addPhase(value);
    setShowNewPhaseDialog(false);
  }

  const addPhase = (name: string) => {
    console.log("Adding Phase");
    const newPhases = Array.from(phases);
    newPhases.push({ "name" : name, "nodes" : [], statistics : { "summary" : null, "measures" : [] } });
    setPhases(newPhases);
    console.log(phases.length);
  };

  const showPhaseDetails = (phase: string) => {
    console.log('Current: ' + selectedPhase + ', New: ' + phase);
    console.log(phases);
    setSelectedPhase(phase);
  };

  const displayNewPhaseDialog = () => {
    setShowNewPhaseDialog(true);
  }

  function getValueStream() {
    const url = 'http://localhost:8080/models/' + props.model + '/valuestream';
    setPhases([]);
    axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*" } })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Value Stream for model: ' + props.model);
        setVStream(data);
        const phases = data.valueStream.phases;
        setPhases(phases);
        // Also initialize activity/phase hashmap
        for (const n of data.valueStream.nodes) {
          activityToPhasesMapping.set(n, null);
        }
        for (const p of phases) {
          for (const n of p.nodes) {
            activityToPhasesMapping.set(n, p.name);
          }
        }    
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  }

  const saveValueStream = (model: string) => {
    const url = 'http://localhost:8080/models/' + model + '/valuestream';
    vStream!.valueStream.phases = phases;
    axios.put(url, vStream, { headers: { 'Content-Type': 'application/json', "Access-Control-Allow-Origin" : "*" } })
      .then(response => response.data)
      .then((data) => {
        console.log('Saving Value Stream: ' + data + '.');
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const resetValueStream = (model: string) => {
    const url = 'http://localhost:8080/models/' + model + '/valuestream';
    axios.delete(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Resetting Value Stream: ' + data + '.');
        setPhases([]);
        setSelectedPhase('');
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const runValueStreamAnalysis = (model: string) => {
    const url = 'http://localhost:8080/models/' + model + '/valuestream/run';
    axios.put(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*" } })
      .then(response => response.data)
      .then((data) => {
        console.log('Run Value Stream Analysis: ' + data + '.');
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  useEffect(() => {
    getValueStream();
  }, []);

  return (
    <>
      <Container style={{ margin: '25px'}}>
        <CardGroup>
          {phases.length > 0 && phases.map((value, index) => (
          <Row>
            <Col md="12">
              <Card border="light" onClick={ () => showPhaseDetails('phase' + index) } style={{ cursor: "pointer" }}>
                <Card.Img variant="left" src="src/images/chevron.png"/>
                <Card.ImgOverlay>
                <Card.Body style={{ textAlign: 'center' }}>
                  <Card.Title style={{ fontSize: 18 }}>{(value as ValueStreamPhaseType).name}</Card.Title>
                  <Card.Text style={{ fontSize: 12 }}>Avg: {(value as ValueStreamPhaseType).name}<br></br>Click for details</Card.Text>
                </Card.Body>
                </Card.ImgOverlay>
              </Card>
            </Col>
          </Row>
          ))}
          <Row>
            <Col md="12">
              <Card border="light" onClick={ () => displayNewPhaseDialog() } style={{ cursor: "pointer" }}>
                <Card.Img variant="left" src="src/images/chevron.png"/>
                <Card.ImgOverlay>
                <Card.Body style={{ textAlign: 'center' }}>
                  <Card.Title style={{ fontSize: 18 }}>Add Phase</Card.Title>
                  <Card.Text style={{ fontSize: 12 }}>Click on the chevron to add new phase</Card.Text>
                </Card.Body>
                </Card.ImgOverlay>
              </Card>
            </Col>
          </Row>
        </CardGroup>
        { showNewPhaseDialog && <NewPhaseDialog showDialog={showNewPhaseDialog} onValueChange={handleNewPhaseName}></NewPhaseDialog>}
        { selectedPhase != "" &&  <Container style={{ margin: '25px'}}>
          <Card border="light">
            <Card.Body>
              <Card.Text style={{fontSize: 14}}><b>{selectedPhase} Details:</b><br></br>
                <Container>
                  <Row>
                  <Col sm="2">
                    - Nodes:<br></br> <Button size="sm">Add Node</Button>
                  </Col>
                  <Col sm="10">
                    - Reword: SCORE.<br></br>
                    - Savings: SCORE.<br></br>
                    - ROI: SCORE.<br></br>
                    fsdfsdfsd sdfasf sdfsdf sfsfds sdfsdf
                  </Col>
                  </Row>
                </Container>
              </Card.Text>
            </Card.Body>
          </Card>
        </Container>}
        <div style={{marginTop: '20px'}}></div>
        <div id="Actions Assets">
          <Button onClick={ ()=> { handleShowPhaseMapping() }} variant="primary">Configure Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { saveValueStream(props.model)}} variant="primary">Save Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { resetValueStream(props.model)}} variant="primary">Delete Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { runValueStreamAnalysis(props.model)}} variant="primary">Run Analysis</Button>
        </div>
        <div>
          <Modal show={showPhaseMapping} onHide={handleShowPhaseMapping} backdrop="static" keyboard={false}
            size="lg" aria-labelledby="contained-modal-title-vcenter" centered>
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
            <Button variant="secondary" onClick={handleClosePhaseMapping}>Close</Button>
            <Button variant="primary" onClick={handleSavePhaseMapping}>Update Phase Mapping(s)</Button>
          </Modal.Footer>
        </Modal>
        </div>
      </Container>
    </>
  );
}

export default ValueStream;
