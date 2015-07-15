/*
 * Copyright 2014 SeaClouds
 * Contact: dev@seaclouds-project.eu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = function (grunt) {

    grunt.initConfig({
        bower: {
            install: {
                options: {
                    targetDir: 'app/static/lib',
                    layout: 'byComponent',
                    bowerOptions: {}
                }
            }
        },

        wiredep: {
            app: {
                src: [
                    'app/index.html'
                ],
                options: {
                    ignorePath: '../bower_components/',
                    fileTypes: {
                        html: {
                            replace: {
                                js: '<script src="static/lib//{{filePath}}"></script>',
                                css: '<link rel="stylesheet" href="static/lib/{{filePath}}" />'
                            }
                        }
                    }
                }
            }
        },

        useminPrepare: {
            html: 'app/index.html',
            options: {
                root: 'app',
                dest: 'app'
            }
        },

        usemin: {
            html: 'app/index.html'
        },

        watch: {
            files: ['bower_components/*'],
            tasks: ['wiredep']
        }
    });

    grunt.loadNpmTasks('grunt-bower-task');
    grunt.loadNpmTasks('grunt-wiredep');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('changes', ['watch']);

    grunt.registerTask('default', [
        'bower',
        'wiredep'
    ]);
};