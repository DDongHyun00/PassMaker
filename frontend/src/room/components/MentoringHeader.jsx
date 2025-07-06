import React from 'react';

const MentoringHeader = () => {
  const handleHelp = () => {
    alert('도움말 페이지로 이동합니다.');
  };

  const handleShare = () => {
    alert('회원 공유 기능입니다.');
  };

  const handleExit = () => {
    if (window.confirm('정말로 인터뷰를 종료하시겠습니까?')) {
      window.location.href = '/mentoringroom';
    }
  };

  return (
    <header className="bg-white shadow-sm border-b border-gray-200 px-6 py-4">
      <div className="flex items-center justify-between">
        {/* 왼쪽 로고 영역 */}
        <div className="flex items-center space-x-6">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-purple-600 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">P</span>
            </div>
            <span className="text-xl font-bold text-gray-900">PassMaker</span>
          </div>
          
          <button 
            onClick={handleHelp}
            className="text-purple-600 hover:text-purple-700 font-medium"
          >
            도움말
          </button>
        </div>

        {/* 오른쪽 버튼 및 프로필 영역 */}
        <div className="flex items-center space-x-4">
          <button 
            onClick={handleShare}
            className="px-4 py-2 bg-purple-600 text-white rounded-full hover:bg-purple-700 transition-colors font-medium"
          >
            회원 공유
          </button>
          
          <button 
            onClick={handleExit}
            className="px-4 py-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors font-medium"
          >
            종료
          </button>

          {/* 사용자 프로필 */}
          <div className="flex items-center space-x-3 ml-4">
            <div className="w-8 h-8 bg-gray-300 rounded-full flex items-center justify-center">
              <span className="text-gray-600 text-sm font-medium">김</span>
            </div>
            <div className="flex items-center space-x-2">
              <span className="text-gray-900 font-medium">김동현</span>
              <div className="w-2 h-2 bg-green-500 rounded-full"></div>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default MentoringHeader;