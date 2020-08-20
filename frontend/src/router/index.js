import Vue from 'vue'
import VueRouter from 'vue-router'

import Index from '../views/Index.vue'

// 포스트
import Home from '../views/post/Home.vue'
import RecentlyPost from '../views/post/RecentlyPost.vue'
import HotPost from '../views/post/HotPost.vue'
import DetailPost from '../views/post/DetailPost.vue'
import UpdateForm from '../views/post/UpdateForm.vue'
import RecentlyQuestion from '../views/post/RecentlyQuestion.vue'
import TrendQuestion from '../views/post/TrendQuestion.vue'

// 유저
import Profile from '../views/user/Profile.vue'
import Edit from '../components/user/EditProfile.vue'

// 블로그
import MyBlog from '../views/blog/MyBlog.vue'
import MyList from '../views/blog/MyList.vue'
import MyQuestions from '../views/blog/MyQuestions.vue'
import BlogCategory from '../views/blog/BlogCategory.vue'
import QuestionPosts from '../views/blog/QuestionPosts.vue'
import BlogPosts from '../views/blog/BlogPosts.vue'
import MyFeeds from '../views/blog/MyFeeds.vue'
import MyAnswers from '../views/blog/MyAnswers.vue'


// 스택오버플로우
import QuestionWrite from '../views/Question/QuestionWrite.vue'
import QuestionDetail from '../views/Question/QuestionDetail.vue'
import QuestionUpdate from '../views/Question/QuestionUpdate.vue'

// 에디터
import Editor from '../views/blog/Editor.vue'

// 검색
import Search from '../components/common/Search.vue'
import SearchQuestions from '../components/common/SearchQuestions.vue'
import TagSearch from '../components/common/TagSearch.vue'
import TagSearchQuestions from '../components/common/TagSearchQuestions.vue'

// about
import About from '../components/common/About.vue'

Vue.use(VueRouter)

const routes = [{
        path: '/',
        name: 'Index',
        component: Index,
        meta: {
            header:1
        },
    },
    {
        path: '/postqna',
        name: 'Home',
        component: Home,
        children: [{
                path: 'recentlypost',
                name: 'RecentlyPost',
                component: RecentlyPost
            },
            {
                path: 'hotpost',
                name: 'HotPost',
                component: HotPost
            },
            {
                path: 'recentlyquestion',
                name: 'RecentlyQuestion',
                component: RecentlyQuestion
            },
            {
                path: 'trendquestion',
                name: 'TrendQuestion',
                component: TrendQuestion
            }
        ]
    },
    {
        path: '/:nickname/:boardName/:post_id/detail',
        name: 'DetailPost',
        component: DetailPost,
        props: true
    },
    {
        path: '/:nickname/:boardName/:post_id/update',
        name: 'UpdateForm',
        component: UpdateForm,
        props: true
    },

    // 블로그 글 헤더검색
    {
        path: '/search',
        name: 'Search',
        component: Search,

    },
    // 질문글 헤더검색
    {
        path: '/search-questions',
        name: 'SearchQuestions',
        component: SearchQuestions,

    },

    // 태그검색
    {
        path: '/tag/:keyword',
        name: 'TagSearch',
        component: TagSearch,

    },

    // 질문 태그검색
    {
        path: '/question-tag/:keyword',
        name: 'TagSearchQuestions',
        component: TagSearchQuestions,

    },

    // 유저 관련
    {
        path: '/user/profile',
        name: 'Profile',
        component: Profile
    },
    {
        path: '/user/profile/edit',
        name: 'Edit',
        component: Edit
    },
    // 블로그 관련
    {
        path: '/:nickname/blog',
        name: 'MyBlog',
        component: MyBlog,
        props: true,
        children: [{
                path: '',
                name: 'MyBlog',
                component: MyList,
                props: true,
                children: [{
                    path: '',
                    name: 'MyBlog',
                    component: BlogPosts
                }]
            },
            {
                path: 'myquestions',
                name: 'MyQuestions',
                component: MyQuestions,
                props: true,
                children: [{
                    path: '',
                    name: 'MyQuestions',
                    component: QuestionPosts
                }]
            },
            {
                path: 'myfeeds',
                name: 'Myfeeds',
                component: MyFeeds,

            },
            {
                path: 'myanswers',
                name: 'MyAnswers',
                component: MyAnswers,

            }

        ]
    },

    // 블로그 에디터
    {
        path: '/blog-editor/:nickname/:category/write',
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
        path: '/questions/ask',
        name: 'QuestionWrite',
        component: QuestionWrite
    },
    {
        path: '/questions/:nickname/:qpostId',
        name: 'QuestionDetail',
        component: QuestionDetail
    },
    {
        path: '/questions/:qpostId',
        name: 'QuestionUpdate',
        component: QuestionUpdate
    },
    // about
    {
        path: '/about',
        name: 'About',
        component: About
    }

]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router