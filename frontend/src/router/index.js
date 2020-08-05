import Vue from 'vue'
import VueRouter from 'vue-router'

// 포스트
import Home from '../views/post/Home.vue'
import PostList from '../views/post/PostList.vue'
import HotPost from '../views/post/HotPost.vue'
import DetailPost from '../views/post/DetailPost.vue'
import UpdateForm from '../views/post/UpdateForm.vue'

// 유저
import Login from '../views/user/Login.vue' // 곧 사라질 예정 모달로 처리함
import Signup from '../views/user/Signup.vue'
import Profile from '../views/user/Profile.vue'

// 블로그
import MyBlog from '../views/blog/MyBlog.vue'
import MyList from '../views/blog/MyList.vue'
import MyQuestions from '../views/blog/MyQuestions.vue'
import MyAnswers from '../views/blog/MyAnswers.vue'
import BlogCategory from '../views/blog/BlogCategory.vue'


// 스택오버플로우
import Question from '../views/Question/Question.vue'
import QuestionWrite from '../views/Question/QuestionWrite.vue'
import QuestionDetail from '../views/Question/QuestionDetail.vue'

// 에디터(임시)
import Editor from '../views/blog/Editor.vue'

Vue.use(VueRouter)

const routes = [{
        path: '/',
        name: 'Home',
        component: Home,
        children: [{
                path: '',
                name: 'Home',
                component: PostList
            },
            {
                path: '/hotpost',
                name: 'HotPost',
                component: HotPost
            }
        ]
    },
    {
        path: '/:nickname/:boardName/:post_id/',
        name: 'DetailPost',
        component: DetailPost,
        props: true
    },
    {
        path: '/:nickname/:boardName/:post_id/update',
        name : 'UpdateForm',
        component: UpdateForm,
        props: true
    },
    // 유저 관련
    {
        path: '/user/login', // 곧 사라질 예정 로그인 모달로 처리함
        name: 'Login',
        component: Login
    },
    {
        path: '/user/signup',
        name: 'Signup',
        component: Signup
    },
    {
        path: '/user/profile',
        name: 'Profile',
        component: Profile
    },
    // 블로그 관련
    {
        path: '/blog/:nickname',
        name: 'MyBlog',
        component: MyBlog,
        props: true,
        children: [{
                path: '',
                name: 'MyBlog',
                component: MyList
            },
            {
                path: 'myquestions',
                name: 'MyQuestions',
                component: MyQuestions
            },
            {
                path: 'myanswers',
                name: 'MyAnswers',
                component: MyAnswers
            }

        ]
    },

    // 블로그 에디터
    {
        path: '/blog-editor/:nickname',
        name: 'BlogEditor',
        component: Editor,
    },
    // 블로그 카테고리
    {
        path: '/blog-category/:nickname',
        name: 'BlogCategory',
        component: BlogCategory,
    },

    // 지식인
    {
        path: '/questions/qlist',
        name: 'Question',
        component: Question
    },
    {
        path: '/questions/ask',
        name: 'QuestionWrite',
        component: QuestionWrite
    },
    {
        path: '/questions/:qpostId',
        name: 'QuestionDetail',
        component: QuestionDetail
    },

]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router