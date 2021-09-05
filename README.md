계속해서 수정을 시도하였는데 이유는 aws에서 실행하기위해 maven으로 빌드하는 방법을 연구하였고 그 과정에서 maven에 build plugin properties등 여러가지 환경설정작업을 하게 되었다. 
겨로가적으로 build태그를 통해 안에 plugin을 작성해서 실행해야될 mainclass를 지정해주고
shade plugin을 통해서 필요한 페키지를 다운받게하여 성공하였다. 
그전에는 utf-8 encoding 문제와 java 버전문제등으로 애먹었다. 다음부터는 확인하고 알아서 해결하자