import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Components/Login/login';
import Home from './Components/Home/Home'; // Import your Home component
import { Container } from 'reactstrap';
import Rightsidebar from './Components/RightSidebar/RightSidebar';
import Sidebar from './Components/Sidebar/Sidebar';
import MainComponent from './Components/MainComponent/MainComponent';
// import './App.css';
function App() {
  return (
    <Router>
      <Rightsidebar />
      <Sidebar />
      <channels />
      <Container>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/main" element={<MainComponent />} />
        </Routes>
      </Container>
    </Router>
  );
}
export default App;
