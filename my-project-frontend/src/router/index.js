import Login from '@/views/welcome/login.vue'
import Register from '@/views/welcome/Register.vue'
import IndexView from '@/views/IndexView.vue'
import WelcomeView from '@/views/WelcomeView.vue'
import { createRouter, createWebHistory } from 'vue-router'
import { unauthorized } from '@/net'
import Reset from '@/views/welcome/Reset.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path:'/',
      name:"welcome",
      component:WelcomeView,
      children:[
        {
          path:'',
          name:"welcome-login",
          component:Login
        },
        {
          path:'register',
          name:"welcome-register",
          component:Register
        },
        {
          path:'reset',
          name:"welcome-reset",
          component:Reset
        }

      ]
    },{
      path:'/index',
      name:'index',
      component:IndexView
    }
  ],
})
router.beforeEach((to,from,next)=>{
    const isUnauthorized = unauthorized();
    if(to.name.startsWith('welcome-') && !isUnauthorized)
    {
      next('/index');
    }else if(to.fullPath.startsWith('/index') && isUnauthorized)
    {
      next('/')
    }
    else next()
})
export default router
