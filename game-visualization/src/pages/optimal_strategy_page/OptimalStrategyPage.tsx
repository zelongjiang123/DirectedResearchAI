import { Arrow } from '../../components/configs';
import MatrixGrid from '../../components/MatrixGrid';

const arrowsPlayer1: Arrow[] = [
    { fromRow: 0, fromCol: 0, toRow: 0, toCol: 1 },
    { fromRow: 0, fromCol: 1, toRow: 1, toCol: 1 },
    { fromRow: 1, fromCol: 1, toRow: 2, toCol: 1 },
    { fromRow: 2, fromCol: 1, toRow: 1, toCol: 1 },
    { fromRow: 1, fromCol: 1, toRow: 0, toCol: 1 },
    { fromRow: 0, fromCol: 1, toRow: 1, toCol: 1 },
    { fromRow: 1, fromCol: 1, toRow: 1, toCol: 0 },
    { fromRow: 1, fromCol: 0, toRow: 0, toCol: 0 },
    { fromRow: 0, fromCol: 0, toRow: 1, toCol: 0 },
    { fromRow: 1, fromCol: 0, toRow: 2, toCol: 0 },
];
  
  const arrowsPlayer2: Arrow[] = [
    { fromRow: 2, fromCol: 2, toRow: 1, toCol: 2 },
    { fromRow: 1, fromCol: 2, toRow: 2, toCol: 2 },
    { fromRow: 2, fromCol: 2, toRow: 1, toCol: 2 },
    { fromRow: 1, fromCol: 2, toRow: 0, toCol: 2 },
    { fromRow: 0, fromCol: 2, toRow: 0, toCol: 1 },
    { fromRow: 0, fromCol: 1, toRow: 0, toCol: 0 },
    { fromRow: 0, fromCol: 0, toRow: 1, toCol: 0 },
    { fromRow: 1, fromCol: 0, toRow: 0, toCol: 0 },
    { fromRow: 0, fromCol: 0, toRow: 1, toCol: 0 },
    { fromRow: 1, fromCol: 0, toRow: 0, toCol: 0 },
  ];
  



function OptimalStrategyPage() {
  return (
    <div className="optimal-strategyPage">
      
      <MatrixGrid arrowsPlayer1={arrowsPlayer1} arrowsPlayer2={arrowsPlayer2} header='Optimal Strategy'/>
    </div>
  );
}

export default OptimalStrategyPage;
