import React, { useEffect, useState } from 'react';
import logo from './logo.svg';
import './App.css';
import OptimalStrategyPage from './pages/optimal_strategy_page/OptimalStrategyPage';
import OptimalPolicyPage from './pages/optimal_policy_page/OptimalPolicyPage';
import NodesGraph from './components/NodesGraph';
import { getGameResult } from './api_calls/apiCall';
import { Arrow, PoliciesGivenOpponentPosition } from './components/configs';
import LoadingPage from './pages/loading_page/LoadingPage';


function App() {
  const [arrows, setArrows] = useState<Arrow[][]>([]);
  const [policies, setPolicies] = useState<PoliciesGivenOpponentPosition[][]>([]);
  const [loading, setLoading] = useState<boolean>(false);

  const handleButtonClick = async () => {
    console.log("click");
    setLoading(true);
    let {arrows, policies} = await getGameResult();
    console.log(arrows)
    setArrows(arrows);
    setPolicies(policies);
    setLoading(false);
  }

  return (
    <div className="App">
      { !loading && 
      <div>
        <OptimalStrategyPage arrows={arrows}/>
        <OptimalPolicyPage policies={policies} />
        <button onClick={()=>{handleButtonClick();}}>Fetch Data</button>
      </div>
      }
      {loading && <LoadingPage/>}
      {/* <NodesGraph rows={9} cols={9} spacing={100}/> */}
    </div>
  );
}

export default App;
