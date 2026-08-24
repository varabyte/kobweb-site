// noinspection JSUnresolvedReference,NpmUsedModulesInstalled

// Kotlin/JS IR emits the whole app as one module, which doesn't play well with webpack's production defaults.
// For one user's large project (https://github.com/Ayfri/Kore/tree/master/website), the following changes resulted in a
// drop from 632s to 16s, in exchange for a modest 10KB increase. For our smaller project, we see a drop from about
// 3m to 2m30s, a 17% speed-up.

;(function () {
    if (config.mode !== 'production') return;
    const TerserPlugin = require('terser-webpack-plugin');
    config.optimization = config.optimization || {};
    config.optimization.concatenateModules = false;
    config.optimization.minimizer = [
        new TerserPlugin({
            extractComments: false,
            // SWC it natively multithreaded, even per-file, providing a speed boost.
            // It is added in build.gradle.kts as a devNpm dependency.
            minify: TerserPlugin.swcMinify,
            terserOptions: {
                compress: true,
                format: { comments: false },
                mangle: true,
            },
        }),
    ];
})();
