import Vue from 'vue'
import App from './App.vue'
import router from './router'

// Bootstrap-vue
import { BootstrapVue, BootstrapVueIcons } from 'bootstrap-vue'

// v-md-editor
import VueMarkdownEditor from '@kangc/v-md-editor';
import '@kangc/v-md-editor/lib/style/base-editor.css';
import vuepressTheme from '@kangc/v-md-editor/lib/theme/vuepress.js';
import enUS from '@kangc/v-md-editor/lib/lang/en-US';

// vue-modal
import VModal from 'vue-js-modal'

// vue-cookies
import VueCookies from 'vue-cookies'

VueMarkdownEditor.use(vuepressTheme);
VueMarkdownEditor.lang.use('en-US', enUS);
Vue.use(VueMarkdownEditor);

Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

Vue.use(VueCookies)

Vue.use(VModal, { dynamic: true })


Vue.config.productionTip = false

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
