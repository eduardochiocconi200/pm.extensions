import { useEffect, useState } from "react";
import { Button, CardGroup, Card, Col, Container, Row } from 'react-bootstrap';
import axios from "axios";

interface ValueStreamType {
  name: string,
  nodes: string[],
  statistics: {
    summary: string,
    measures: string[]
  }
}

function ValueStream(props: { model: string; }) {
  const [selectedPhase, setSelectedPhase] = useState<string>('');
  const [phases, setPhases] = useState<ValueStreamType[]>([]);

  function getValueStream() {
    const url = 'http://localhost:8080/models/' + props.model + '/valuestream';
    setPhases([]);
    axios.get(url, { headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Retrieved Value Stream for model: ' + props.model);
        const phases = data.valueStream.phases;
        setPhases(phases);
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  }

  const saveValueStream = (model: string) => {
    const url = 'http://localhost:8080/models/' + model + '/valuestream';
    axios.put(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
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
    axios.put(url, { responseType: "blob", headers: { "Access-Control-Allow-Origin" : "*"} })
      .then(response => response.data)
      .then((data) => {
        console.log('Run Value Stream Analysis: ' + data + '.');
      })
      .catch((err) => {
        console.log("AXIOS ERROR: ", err)
      })
  };

  const addPhase = () => {
    console.log("Adding Phase");
    const newPhases = Array.from(phases);
    newPhases.push({ "name" : "Phase " + (phases.length+1), "nodes" : [], statistics : { "summary" : "Summary", "measures" : [] } });
    setPhases(newPhases);
    console.log(phases.length);
  };

  const showPhaseDetails = (phase: string) => {
    console.log('Current: ' + selectedPhase + ', New: ' + phase);
    console.log(phases);
    setSelectedPhase(phase);
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
                  <Card.Title style={{ fontSize: 18 }}>{(value as ValueStreamType).name}</Card.Title>
                  <Card.Text style={{ fontSize: 12 }}>Avg: {(value as ValueStreamType).name}<br></br>Click for details</Card.Text>
                </Card.Body>
                </Card.ImgOverlay>
              </Card>
            </Col>
          </Row>
          ))}
          <Row>
            <Col md="12">
              <Card border="light" onClick={ () => addPhase() } style={{ cursor: "pointer" }}>
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
        {selectedPhase != "" &&  <Container style={{ margin: '25px'}}>
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
          <Button onClick={ ()=> { saveValueStream(props.model)}} variant="primary">Save</Button>
          {' '}
          <Button onClick={ ()=> { resetValueStream(props.model)}} variant="primary">Clear</Button>
          {' '}
          <Button onClick={ ()=> { runValueStreamAnalysis(props.model)}} variant="primary">Run Analysis</Button>
        </div>
      </Container>
    </>
  );
}

export default ValueStream;
