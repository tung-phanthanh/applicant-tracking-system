document.addEventListener('DOMContentLoaded', () => {
    const appContainer = document.getElementById('app');
    const isLoginPage = window.location.pathname.includes('login.html');

    if (isLoginPage || !appContainer) {
        if (!isLoginPage) console.warn('No #app container found, skipping layout injection');
        lucide.createIcons();
        return;
    }

    // Capture the page-specific content
    const pageContent = appContainer.innerHTML;
    const pageTitle = appContainer.getAttribute('data-title') || 'Dashboard';

    // Define the Layout Template
    const layoutHTML = `
    <div class="flex h-screen overflow-hidden bg-gray-50">
        <!-- Sidebar -->
        <aside class="w-64 bg-slate-900 text-white flex flex-col shrink-0 transition-all duration-300" id="sidebar">
            <div class="h-16 flex items-center px-6 border-b border-slate-800">
                <span class="text-xl font-bold tracking-tight text-white flex items-center gap-2">
                    <i data-lucide="hexagon" class="w-6 h-6 text-blue-500 fill-blue-500"></i>
                    ATS Enterprise
                </span>
            </div>
            <nav class="flex-1 px-4 py-4 space-y-1">
                <a href="index.html" class="nav-link group flex items-center px-2 py-2 text-sm font-medium rounded-md text-slate-300 hover:bg-slate-800 hover:text-white transition-colors" data-page="index.html">
                    <i data-lucide="layout-dashboard" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    Dashboard
                </a>
                <a href="jobs.html" class="nav-link group flex items-center px-2 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white rounded-md transition-colors" data-page="jobs.html">
                    <i data-lucide="briefcase" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    Jobs
                </a>
                <a href="candidates-kanban.html" class="nav-link group flex items-center px-2 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white rounded-md transition-colors" data-page="candidates-kanban.html">
                    <i data-lucide="users" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    Candidates
                </a>
                 <a href="interview-calendar.html" class="nav-link group flex items-center px-2 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 hover:text-white rounded-md transition-colors" data-page="interview-calendar.html">
                    <i data-lucide="calendar" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    Interviews
                </a>
            </nav>
            <div class="p-4 border-t border-slate-800">
                <a href="profile.html" class="nav-link group flex items-center w-full px-2 py-2 text-sm font-medium text-slate-300 hover:text-white rounded-md transition-colors" data-page="profile.html">
                    <i data-lucide="user" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    My Profile
                </a>
                <a href="login.html" class="group flex items-center w-full px-2 py-2 text-sm font-medium text-slate-300 hover:text-white mt-1 rounded-md transition-colors">
                    <i data-lucide="log-out" class="w-5 h-5 mr-3 group-hover:text-white transition-colors"></i>
                    Sign out
                </a>
            </div>
        </aside>

        <!-- Main Content Wrapper -->
        <div class="flex-1 flex flex-col min-w-0 overflow-hidden">
            <!-- Header -->
            <header class="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4 sm:px-6 lg:px-8 shrink-0">
                <div class="flex items-center gap-4">
                    <button id="mobile-menu-btn" class="md:hidden p-2 rounded-md text-gray-400 hover:text-gray-500 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-blue-500">
                        <i data-lucide="menu" class="w-6 h-6"></i>
                    </button>
                    <h1 class="text-2xl font-bold text-slate-900 truncate">${pageTitle}</h1>
                </div>
                
                <div class="flex items-center space-x-4">
                    <!-- Notification Dropdown -->
                    <div class="relative">
                        <button id="notification-btn" class="p-2 text-slate-500 hover:text-slate-700 relative focus:outline-none bg-white rounded-full hover:bg-gray-100 transition-colors">
                            <i data-lucide="bell" class="w-6 h-6"></i>
                            <span class="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full ring-2 ring-white"></span>
                        </button>
                        
                        <!-- Dropdown Menu -->
                        <div id="notification-menu" class="hidden absolute right-0 mt-2 w-80 bg-white rounded-xl shadow-lg ring-1 ring-black ring-opacity-5 z-50 transform origin-top-right transition-all duration-200">
                            <div class="px-4 py-3 border-b border-gray-100 flex justify-between items-center bg-gray-50 rounded-t-xl">
                                <span class="text-sm font-semibold text-gray-900">Notifications</span>
                                <button class="text-xs font-medium text-blue-600 hover:text-blue-500 hover:underline">Mark all read</button>
                            </div>
                            <div class="max-h-[300px] overflow-y-auto">
                                <a href="#" class="block px-4 py-3 hover:bg-gray-50 border-b border-gray-50 last:border-0 relative transition-colors">
                                    <div class="flex items-start">
                                        <div class="flex-shrink-0 pt-1">
                                            <div class="h-2.5 w-2.5 rounded-full bg-blue-500 ring-4 ring-blue-50"></div>
                                        </div>
                                        <div class="ml-3 w-0 flex-1">
                                            <p class="text-sm font-medium text-gray-900">New candidate applied</p>
                                            <p class="text-xs text-slate-500 mt-0.5">Sarah Jenkins applied for Senior React Developer</p>
                                            <p class="text-xs text-slate-400 mt-1">10 min ago</p>
                                        </div>
                                    </div>
                                </a>
                                <a href="#" class="block px-4 py-3 hover:bg-gray-50 transition-colors">
                                    <div class="flex items-start">
                                        <div class="flex-shrink-0 pt-1">
                                            <!-- Read state placeholder -->
                                        </div>
                                        <div class="ml-3 w-0 flex-1">
                                            <p class="text-sm font-medium text-gray-900 text-opacity-70">Interview confirmed</p>
                                            <p class="text-xs text-slate-500 mt-0.5">Interview with Mike Ross scheduled for tomorrow</p>
                                            <p class="text-xs text-slate-400 mt-1">1 hour ago</p>
                                        </div>
                                    </div>
                                </a>
                            </div>
                            <div class="px-4 py-2 border-t border-gray-100 text-center bg-gray-50 rounded-b-xl">
                                <a href="#" class="text-xs font-medium text-blue-600 hover:text-blue-500">View all notifications</a>
                            </div>
                        </div>
                    </div>

                    <div class="h-9 w-9 rounded-full bg-slate-100 flex items-center justify-center text-slate-600 font-bold cursor-pointer hover:bg-slate-200 ring-2 ring-white shadow-sm transition-colors">
                        JD
                    </div>
                </div>
            </header>

            <!-- Page Content Injection -->
            <main class="flex-1 overflow-y-auto bg-gray-50 p-4 sm:p-6 lg:p-8" id="main-content">
                ${pageContent}
            </main>
        </div>
    </div>
    `;

    // Inject Layout
    document.body.innerHTML = layoutHTML;

    // Initialize Icons
    lucide.createIcons();

    // Set Active Link
    const path = window.location.pathname.split('/').pop() || 'index.html';
    const links = document.querySelectorAll('.nav-link');
    links.forEach(link => {
        const linkPage = link.getAttribute('data-page');
        if (path === linkPage || (path === '' && linkPage === 'index.html')) {
            link.classList.add('bg-slate-800', 'text-white');
            link.classList.remove('text-slate-300');
            // Ensure icon is white
            const icon = link.querySelector('i');
            if (icon) icon.classList.add('text-white');
        }
    });

    // Notification Logic
    const notificationBtn = document.getElementById('notification-btn');
    const notificationMenu = document.getElementById('notification-menu');

    if (notificationBtn && notificationMenu) {
        notificationBtn.addEventListener('click', (e) => {
            e.stopPropagation();
            notificationMenu.classList.toggle('hidden');
        });

        document.addEventListener('click', (e) => {
            if (!notificationMenu.contains(e.target) && !notificationBtn.contains(e.target)) {
                notificationMenu.classList.add('hidden');
            }
        });
    }

    // Mobile Menu Logic (Basic Toggle)
    const mobileMenuBtn = document.getElementById('mobile-menu-btn');
    const sidebar = document.getElementById('sidebar'); // We'd need to adjust sidebar CSS for mobile to work fully

    // For now, logging ensuring setup is correct
    console.log('Layout injected successfully');
});
