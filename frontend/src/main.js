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
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import createHighlightLinesPlugin from '@kangc/v-md-editor/lib/plugins/highlight-lines/index';
import 'prismjs/components/prism-json';
// import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';


// vue-modal
import VModal from 'vue-js-modal'

// vue-cookies
import VueCookies from 'vue-cookies'

// vue-calendar
import VueCalendarHeatmap from 'vue-calendar-heatmap'

// vue-infinite-loading
import InfiniteLoading from 'vue-infinite-loading'

// vue-swich button
import ToggleButton from 'vue-js-toggle-button'

// vue-chart
import VueApexCharts from 'vue-apexcharts'


VueMarkdownEditor.use(vuepressTheme);
VueMarkdownEditor.lang.use('en-US', enUS);
VueMarkdownEditor.use(createHighlightLinesPlugin());
VMdPreview.use(vuepressTheme);
Vue.use(VueMarkdownEditor);
Vue.use(VMdPreview);

Vue.use(BootstrapVue)
Vue.use(BootstrapVueIcons)

Vue.use(VueCookies)

Vue.use(VModal, { dynamic: true })

Vue.use(VueCalendarHeatmap)

Vue.use(InfiniteLoading)

Vue.use(ToggleButton)

Vue.use(VueApexCharts)



Vue.config.productionTip = false

Vue.component('apexchart', VueApexCharts)
new Vue({
    router,
    render: h => h(App)
}).$mount('#app')