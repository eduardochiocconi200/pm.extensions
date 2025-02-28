import { useEffect, useState } from "react";
import { Button, CardGroup, Card, Col, Container, Row } from 'react-bootstrap';
import axios from "axios";

import NewPhaseDialog from "./NewPhaseDialog";
import PhaseDetails from "./PhaseDetails";
import ActivityToPhaseMapping from "./ActivityToPhaseMapping";

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
  const [vStream, setVStream] = useState<VStreamType>();
  const [phases, setPhases] = useState<ValueStreamPhaseType[]>([]);
  const [selectedPhase, setSelectedPhase] = useState<ValueStreamPhaseType>();
  const [showPhaseMapping, setShowPhaseMapping] = useState(false);
  const [showNewPhaseDialog, setShowNewPhaseDialog] = useState(false);
  const [activityToPhasesMapping, setActivityToPhasesMapping] = useState(new Map<string,ValueStreamPhaseType|null>());

  const phaseChanged = (event) => {
    const activity = event.target.id;
    const phase = event.target.value;
    const newMap = activityToPhasesMapping;
    newMap.delete(activity);
    newMap.set(activity, phase);
    setActivityToPhasesMapping(newMap);
  }

  const handleShowPhaseMapping = (show : boolean, save: boolean) => {
    setShowPhaseMapping(show);
    if (save) {
      console.log('Will synchronize with Value Stream.');
      if (phases !== null) {
        console.log(phases);
        for (const p of phases) {
          console.log(p);
          p.nodes = [];
        }
        for (const [key, value] of activityToPhasesMapping.entries()) {
          if (value !== null) {
            console.log('Key: (' + key);
            console.log('Value: (' + value);
            for (const p in phases) {
              if (p.name === value) {
                p.nodes.push(key);
              }
            }
          }
        }    
      }
    }
  }

  const handleNewPhaseName = (value : string) => {
    if (value !== null && value != "") {
      addPhase(value);
    }
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
    setSelectedPhase(phases.find(ph => ph.name === phase));
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
            activityToPhasesMapping.set(n, p);
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
        setSelectedPhase(undefined);
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
              <Card border="light" key={index} onClick={ () => showPhaseDetails(value.name) } style={{ cursor: "pointer" }}>
                <Card.Img variant="left" src="src/images/chevron.png"/>
                <Card.ImgOverlay>
                <Card.Body style={{ textAlign: 'center' }}>
                  <Card.Title style={{ fontSize: 18 }}>{(value as ValueStreamPhaseType).name}</Card.Title>
                  <Card.Text style={{ fontSize: 12 }}>Avg: {(value as ValueStreamPhaseType).statistics.summary != null ? (value as ValueStreamPhaseType).statistics.summary.averageTime : '-'}<br></br>Click for details</Card.Text>
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
        { selectedPhase != undefined && <PhaseDetails phase={selectedPhase}></PhaseDetails>}
        <div style={{marginTop: '20px'}}></div>
        <div id="Actions Assets">
          <Button onClick={ ()=> { handleShowPhaseMapping(true, false) }} variant="primary">Configure Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { saveValueStream(props.model)}} variant="primary">Save Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { resetValueStream(props.model)}} variant="primary">Delete Value Stream</Button>
          {' '}
          <Button onClick={ ()=> { runValueStreamAnalysis(props.model)}} variant="primary">Run Analysis</Button>
        </div>
        <div>
          {showPhaseMapping && <ActivityToPhaseMapping showPhaseMapping={showPhaseMapping} handleShowPhaseMapping={handleShowPhaseMapping} phaseChanged={phaseChanged} phases={phases} activityToPhasesMapping={activityToPhasesMapping}></ActivityToPhaseMapping>}
        </div>
      </Container>
    </>
  );
}

export default ValueStream;
