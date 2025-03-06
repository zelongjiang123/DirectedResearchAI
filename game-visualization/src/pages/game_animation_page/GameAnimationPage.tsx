import GameAnimation from "../../components/GameAnimation";

interface GameAnimationPageProps {
    positions1: [number, number][], 
    positions2: [number, number][],
};


const GameAnimationPage: React.FC<GameAnimationPageProps> = ({
    positions1,
    positions2,
}) => {
    return (
        <div className="game-animation-page">
            <h1>Game Animation</h1>
            <GameAnimation positions1={positions1} positions2={positions2}/>
        </div>
    );
}

export default GameAnimationPage;