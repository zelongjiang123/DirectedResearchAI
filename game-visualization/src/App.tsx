import React from 'react';
import logo from './logo.svg';
import './App.css';
import OptimalStrategyPage from './pages/optimal_strategy_page/OptimalStrategyPage';
import OptimalPolicyPage from './pages/optimal_policy_page/OptimalPolicyPage';


function App() {
  return (
    <div className="App">
      <OptimalStrategyPage/>
      <OptimalPolicyPage/>
    </div>
  );
}

export default App;
